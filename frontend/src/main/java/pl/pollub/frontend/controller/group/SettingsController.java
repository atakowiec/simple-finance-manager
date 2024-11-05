package pl.pollub.frontend.controller.group;

import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.ViewParameter;
import pl.pollub.frontend.controller.home.add.ColorListCell;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.service.ColorsService;
import pl.pollub.frontend.service.HttpService;
import pl.pollub.frontend.util.JsonUtil;

import java.net.http.HttpResponse;

public class SettingsController {
    @ViewParameter("group")
    private Group group;

    @Inject
    private ColorsService colorsService;
    @Inject
    private HttpService httpService;
    @Inject
    private EventEmitter eventEmitter;

    @FXML
    private TextField groupNameField;
    @FXML
    private ComboBox<String> colorPicker;
    @FXML
    private TextField expenseLimitField;
    @FXML
    private Label nameError;
    @FXML
    private Label colorError;
    @FXML
    private Label expenseLimitError;

    @PostInitialize
    public void postInitialize() {
        colorPicker.getItems().addAll(colorsService.getColors());

        colorPicker.setCellFactory(param -> new ColorListCell());
        colorPicker.setButtonCell(new ColorListCell(group.getColor()));

        groupNameField.setText(group.getName());

        boolean expenseLimitSet = group.getExpenseLimit() != null && group.getExpenseLimit() > 0;

        expenseLimitField.setText(expenseLimitSet ? String.valueOf(group.getExpenseLimit()) : "");
    }

    public void saveName() {
        if (groupNameField.getText().isEmpty()) {
            nameError.setText("Wpisz nazwę!");
            return;
        }

        HttpResponse<String> response = httpService.patch("/groups/" + group.getId() + "/name", groupNameField.getText());

        if (response.statusCode() != 200) {
            nameError.setText("Wystąpił błąd podczas zmiany nazwy!");
            return;
        }

        JsonObject jsonResponse = JsonUtil.fromJson(response.body()).getAsJsonObject();

        group.setName(jsonResponse.get("name").getAsString());
        nameError.setText("Zapisano!");
        eventEmitter.emit(EventType.GROUPS_UPDATE);
    }

    public void saveColor() {
        if (colorPicker.getValue() == null) {
            colorError.setText("Wybierz kolor!");
            return;
        }

        HttpResponse<String> response = httpService.patch("/groups/" + group.getId() + "/color", colorPicker.getValue());

        if (response.statusCode() != 200) {
            colorError.setText("Wystąpił błąd podczas zmiany koloru!");
            return;
        }

        JsonObject jsonResponse = JsonUtil.fromJson(response.body()).getAsJsonObject();

        group.setColor(jsonResponse.get("color").getAsString());
        colorError.setText("Zapisano!");
        eventEmitter.emit(EventType.GROUPS_UPDATE);
    }

    public void saveExpenseLimit() {
        if (expenseLimitField.getText().isEmpty()) {
            expenseLimitError.setText("Wpisz limit!");
            return;
        }

        try {
            double limit = Double.parseDouble(expenseLimitField.getText());

            if (limit < 0) {
                expenseLimitError.setText("Limit nie może być mniejszy od 0!");
                return;
            }

            HttpResponse<String> response = httpService.patch("/groups/" + group.getId() + "/expense-limit", limit);

            if (response.statusCode() != 200) {
                expenseLimitError.setText("Wystąpił błąd podczas zmiany limitu!");
                return;
            }

            JsonObject jsonResponse = JsonUtil.fromJson(response.body()).getAsJsonObject();

            group.setExpenseLimit(jsonResponse.get("limit").getAsDouble());
            expenseLimitError.setText("Zapisano!");
            eventEmitter.emit(EventType.GROUPS_UPDATE);
        } catch (NumberFormatException ignored) {
            expenseLimitError.setText("Limit musi być liczbą!");
        }
    }
}
