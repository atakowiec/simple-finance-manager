package pl.pollub.frontend.controller.group.add.list;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import pl.pollub.frontend.model.transaction.TransactionCategory;

public class CategoryCellController {
    @FXML
    private ImageView categoryIcon;

    @FXML
    private Label nameLabel;

    @FXML
    private javafx.scene.layout.HBox container;

    public void setCategory(TransactionCategory category) {
        nameLabel.setText(category.getName());
        categoryIcon.setImage(category.getImageIcon());
        if (container != null) {
            container.setPadding(new javafx.geometry.Insets(0, 0, 0, category.getDepth() * 20));
        }
    }
}
