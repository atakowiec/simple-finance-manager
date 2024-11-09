package pl.pollub.frontend.controller.group.transaction;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.Getter;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.transaction.Expense;
import pl.pollub.frontend.model.transaction.Income;
import pl.pollub.frontend.model.transaction.Transaction;
import pl.pollub.frontend.service.TransactionService;

import java.time.format.DateTimeFormatter;

import javafx.scene.control.Button;

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

    @FXML
    private Button deleteButton;

    @Inject
    private TransactionService transactionService;

    @Inject
    private EventEmitter eventEmitter;


    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;

        nameLabel.setText(transaction.getName());
        dateLabel.setText(transaction.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        amountLabel.setText(transaction.getAmountWithSign() + " zł");
        amountLabel.getStyleClass().add(transaction.getAmountStyleClass());
        categoryIcon.setImage(transaction.getCategory() == null ? null : transaction.getCategory().getImageIcon());

        deleteButton.setOnAction(event -> deleteTransaction());

    }

    private void deleteTransaction() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Potwierdzenie usunięcia");
        confirmationAlert.setHeaderText("Usuwanie transakcji");
        confirmationAlert.setContentText("Czy na pewno chcesz usunąć tę transakcję?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (!response.getButtonData().isDefaultButton()) {
                return;
            }
            if (transaction instanceof Income) {
                var deleteResponse = transactionService.deleteIncome((long) transaction.getId());
                if (deleteResponse.statusCode() == 200) {
                    eventEmitter.emit(EventType.TRANSACTION_UPDATE);
                } else {
                    showError("Błąd podczas usuwania przychodu: " + deleteResponse.body());
                }
            } else if (transaction instanceof Expense) {
                var deleteResponse = transactionService.deleteExpense((long) transaction.getId());
                if (deleteResponse.statusCode() == 200) {
                    eventEmitter.emit(EventType.TRANSACTION_UPDATE);
                } else {
                    showError("Błąd podczas usuwania wydatku: " + deleteResponse.body());
                }
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText("Wystąpił problem");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
