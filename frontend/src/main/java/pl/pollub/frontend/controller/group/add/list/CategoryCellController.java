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

    public void setCategory(TransactionCategory category) {
        nameLabel.setText(category.getName());
        categoryIcon.setImage(category.getIcon());
    }
}
