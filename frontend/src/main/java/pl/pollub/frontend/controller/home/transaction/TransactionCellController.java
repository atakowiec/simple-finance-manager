package pl.pollub.frontend.controller.home.transaction;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.Getter;
import pl.pollub.frontend.model.transaction.Transaction;

import java.time.format.DateTimeFormatter;

@Getter
public class TransactionCellController {
    private Transaction transaction;

    @FXML
    private ImageView categoryIcon;

    @FXML
    private Label nameLabel;

    @FXML
    private Label amountLabel;

    @FXML
    private Label dateLabel;

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;

        nameLabel.setText(transaction.getName());
        dateLabel.setText(transaction.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        amountLabel.setText(transaction.getAmountWithSign() + " zł");
        amountLabel.getStyleClass().add(transaction.getAmountStyleClass());
        categoryIcon.setImage(transaction.getCategory() == null ? null : transaction.getCategory().getIcon());
    }
}
