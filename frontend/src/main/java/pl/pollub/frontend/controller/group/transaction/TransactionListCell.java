package pl.pollub.frontend.controller.group.transaction;


import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.transaction.Expense;
import pl.pollub.frontend.model.transaction.Income;
import pl.pollub.frontend.model.transaction.Transaction;
import pl.pollub.frontend.service.ModalService;
import pl.pollub.frontend.service.ModalService;

import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.Map;

public class TransactionListCell extends ListCell<Transaction> {
    private final HBox root;
    private final TransactionCellController controller;
    private Transaction transaction;

    @Inject
    private ModalService modalService;
    @Inject
    private DependencyInjector dependencyInjector;

    public TransactionListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(FinanceApplication.class.getResource("components/transaction-list-cell.fxml"));
            root = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostInitialize
    private void postInitialize() {
        dependencyInjector.manualInject(controller);
    }

    @Override
    protected void updateItem(Transaction item, boolean empty) {
        super.updateItem(item, empty);

        this.transaction = item;

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        root.setOnMouseClicked(this::onClick);

        controller.setTransaction(item);
        setGraphic(root);
    }

    private void onClick(MouseEvent mouseEvent) {
        if (transaction instanceof Expense) {
            modalService.showModal("group/edit-expense.fxml", Map.of("transaction", transaction));
            return;
        }
        if (transaction instanceof Income) {
            modalService.showModal("group/edit-income.fxml", Map.of("transaction", transaction));
            return;
        }

        throw new IllegalArgumentException("Unknown transaction type");
    }
}
