package pl.pollub.frontend.controller.group;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.controller.group.transaction.TransactionListCell;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.model.transaction.Transaction;
import pl.pollub.frontend.service.TransactionService;

public class TransactionsController extends AbstractGroupController {
    @FXML
    private ListView<Transaction> mainList;

    @Inject
    private TransactionService transactionService;
    @Inject
    private DependencyInjector dependencyInjector;

    @PostInitialize
    public void postInitialize() {
        onTransactionUpdate();

        mainList.setCellFactory(param -> {
            TransactionListCell transactionListCell = new TransactionListCell();
            dependencyInjector.manualInject(transactionListCell);
            dependencyInjector.runPostInitialize(transactionListCell);
            return transactionListCell;
        });
    }

    @OnEvent(EventType.TRANSACTION_UPDATE)
    public void onTransactionUpdate() {
        Group group = getGroup();

        mainList.getItems().clear();
        mainList.getItems().addAll(transactionService.fetchExpenses(group.getId()));
        mainList.getItems().addAll(transactionService.fetchIncomes(group.getId()));

        mainList.getItems().sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
    }
}
