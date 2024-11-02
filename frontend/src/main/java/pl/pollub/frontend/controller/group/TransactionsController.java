package pl.pollub.frontend.controller.group;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.ViewParameter;
import pl.pollub.frontend.controller.group.transaction.TransactionListCell;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.model.transaction.Transaction;
import pl.pollub.frontend.service.TransactionService;

public class TransactionsController {
    @ViewParameter("group")
    private Group group;

    @FXML
    private ListView<Transaction> mainList;

    @Inject
    private TransactionService transactionService;

    @PostInitialize
    public void postInitialize() {
        onTransactionUpdate();

        mainList.setCellFactory(param -> new TransactionListCell());
    }

    @OnEvent(EventType.TRANSACTION_UPDATE)
    public void onTransactionUpdate() {
        mainList.getItems().clear();
        mainList.getItems().addAll(transactionService.fetchExpenses(group.getId()));
        mainList.getItems().addAll(transactionService.fetchIncomes(group.getId()));

        mainList.getItems().sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
    }
}
