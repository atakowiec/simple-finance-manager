package pl.pollub.frontend.controller.group.add;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.controller.group.AbstractGroupController;
import pl.pollub.frontend.controller.group.add.list.CategoryListCell;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.transaction.TransactionCategory;
import pl.pollub.frontend.service.TransactionService;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public abstract class AbstractAddTransactionController extends AbstractGroupController {
    @FXML
    private TextField nameInput;

    @FXML
    private TextField amountInput;

    @FXML
    private ComboBox<TransactionCategory> categoryPicker;

    @FXML
    public DatePicker datePicker;

    @FXML
    public Label errorLabel;

    @Inject
    protected TransactionService transactionService;

    @Inject
    private EventEmitter eventEmitter;

    @PostInitialize
    public void superPostInitialize() {
        categoryPicker.getItems().addAll(getCategories());

        categoryPicker.setCellFactory(param -> new CategoryListCell());
        categoryPicker.setButtonCell(new CategoryListCell());

        datePicker.setValue(LocalDate.now());
    }

    public void save() {
        setError("");

        String name = nameInput.getText();
        String amount = amountInput.getText();
        TransactionCategory category = categoryPicker.getValue();
        LocalDate date = datePicker.getValue();

        if (name.isEmpty() || amount.isEmpty() || category == null) {
            setError("Wszystkie pola są wymagane!");
            return;
        }

        double amountValue;
        try {
            amountValue = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            setError("Niepoprawna kwota!");
            return;
        }

        if(amountValue <= 0) {
            setError("Kwota musi być większa od zera!");
            return;
        }

        HttpResponse<String> response = addTransaction(name, amountValue, category, date);

        if(response.statusCode() != 201) {
            setError("Wystąpił błąd podczas dodawania transakcji!");
            return;
        }

        eventEmitter.emit(EventType.TRANSACTION_UPDATE);
        setError("Zapisano");
    }

    protected void setError(String message) {
        errorLabel.setText(message);
    }

    protected abstract HttpResponse<String> addTransaction(String name, double amount, TransactionCategory category, LocalDate date);

    protected abstract List<TransactionCategory> getCategories();
}
