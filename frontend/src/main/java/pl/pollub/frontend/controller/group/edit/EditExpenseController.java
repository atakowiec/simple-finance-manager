package pl.pollub.frontend.controller.group.edit;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.ViewParameter;
import pl.pollub.frontend.controller.group.add.list.CategoryListCell;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.transaction.Transaction;
import pl.pollub.frontend.model.transaction.TransactionCategory;
import pl.pollub.frontend.service.CategoryService;
import pl.pollub.frontend.service.TransactionService;

import java.time.LocalDate;
import java.util.List;

public class EditExpenseController {

    @FXML
    private TextField nameInput;

    @FXML
    private TextField amountInput;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<TransactionCategory> categoryPicker;

    @FXML
    private Label errorLabel;

    @Inject
    private TransactionService transactionService;

    @Inject
    private CategoryService categoryService;

    @ViewParameter("transaction")
    private Transaction transaction;

    @Inject
    private EventEmitter eventEmitter;

    @PostInitialize
    public void initializeData() {
        categoryPicker.setCellFactory(param -> new CategoryListCell());
        categoryPicker.setButtonCell(new CategoryListCell());

        loadExpenseData();
        loadCategories();
    }

    private void loadExpenseData() {
        nameInput.setText(transaction.getName());
        amountInput.setText(String.valueOf(transaction.getAmount()));
        datePicker.setValue(transaction.getLocalDate());
    }

    private void loadCategories() {
        List<TransactionCategory> expenseCategories = categoryService.getCategories(transaction);
        categoryPicker.getItems().setAll(expenseCategories);

        categoryPicker.setValue(transaction.getCategory());
    }

    @FXML
    private void save() {
        String name = nameInput.getText();
        String amountText = amountInput.getText();
        TransactionCategory category = categoryPicker.getValue();
        LocalDate date = datePicker.getValue();

        if (name.isEmpty() || amountText.isEmpty() || category == null || date == null) {
            errorLabel.setText("Wszystkie pola są wymagane.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                errorLabel.setText("Kwota musi być dodatnia.");
                return;
            }

            Long incomeId = (long) transaction.getId();
            var response = transactionService.updateExpense(incomeId, name, amount, category, date, transaction.getGroupId());

            if (response.statusCode() == 200) {
                errorLabel.setText("Wydatek został zaktualizowany.");
                eventEmitter.emit(EventType.TRANSACTION_UPDATE);
            } else {
                errorLabel.setText("Błąd podczas aktualizacji: " + response.body());
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Niepoprawna kwota.");
        } catch (Exception e) {
            errorLabel.setText("Błąd podczas aktualizacji wydatku.");
        }
    }
}
