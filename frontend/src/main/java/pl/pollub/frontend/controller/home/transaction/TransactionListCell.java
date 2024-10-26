package pl.pollub.frontend.controller.home.transaction;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.model.transaction.Transaction;

import java.io.IOException;

public class TransactionListCell extends ListCell<Transaction> {
    private final HBox root;
    private final TransactionCellController controller;

    public TransactionListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(FinanceApplication.class.getResource("components/transaction-list-cell.fxml"));
            root = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(Transaction item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        controller.setTransaction(item);
        setGraphic(root);
    }
}
