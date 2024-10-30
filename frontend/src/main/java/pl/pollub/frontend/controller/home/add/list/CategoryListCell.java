package pl.pollub.frontend.controller.home.add.list;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.model.transaction.TransactionCategory;

import java.io.IOException;

public class CategoryListCell extends ListCell<TransactionCategory> {
    private final HBox root;
    private final CategoryCellController controller;

    public CategoryListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(FinanceApplication.class.getResource("components/category-picker-element.fxml"));
            root = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(TransactionCategory item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        controller.setCategory(item);
        setGraphic(root);
    }
}
