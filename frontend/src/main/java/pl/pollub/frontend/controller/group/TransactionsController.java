package pl.pollub.frontend.controller.group;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.controller.group.transaction.TransactionListCell;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.model.transaction.Transaction;
import pl.pollub.frontend.service.TransactionService;

import java.util.Objects;

public class TransactionsController extends AbstractGroupController {
    @FXML
    private ListView<Transaction> mainList;

    @Inject
    private TransactionService transactionService;

    @PostInitialize
    public void postInitialize() {
        onTransactionUpdate(getGroup().getId());

        mainList.setCellFactory(param -> new TransactionListCell());
    }

    @OnEvent(EventType.TRANSACTION_UPDATE)
    public void onTransactionUpdate(Long groupId) {
        Group group = getGroup();
        if(!Objects.equals(group.getId(), groupId))
            return;

        mainList.getItems().clear();
        mainList.getItems().addAll(transactionService.fetchExpenses(group.getId()));
        mainList.getItems().addAll(transactionService.fetchIncomes(group.getId()));

        mainList.getItems().sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
    }
}
