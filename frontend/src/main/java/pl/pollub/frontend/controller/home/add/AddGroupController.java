package pl.pollub.frontend.controller.home.add;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.service.ColorsService;
import pl.pollub.frontend.service.GroupsService;
import pl.pollub.frontend.service.ModalService;

import java.net.http.HttpResponse;

public class AddGroupController {
    @FXML
    private TextField nameInput;

    @FXML
    private ComboBox<String> colorPicker;

    @FXML
    private Label errorLabel;

    @Inject
    protected GroupsService groupsService;

    @Inject
    protected ColorsService colorsService;

    @Inject
    private ModalService modalService;

    @Inject
    private EventEmitter eventEmitter;

    @PostInitialize
    public void postInitialize() {
        colorPicker.getItems().addAll(colorsService.getColors());

        colorPicker.setCellFactory(param -> new ColorListCell());
        colorPicker.setButtonCell(new ColorListCell());
    }

    public void save() {
        setError("");

        String name = nameInput.getText();
        String color = colorPicker.getValue();

        if (name.isEmpty() || color == null || color.isEmpty()) {
            setError("Wszystkie pola są wymagane!");
            return;
        }

        HttpResponse<String> response = addGroup(name, color);

        if(response.statusCode() != 201) {
            setError("Wystąpił błąd podczas dodawania groupy!");
            return;
        }

        eventEmitter.emit(EventType.GROUPS_UPDATE);

        modalService.hideModal();
    }

    private HttpResponse<String> addGroup(String name, String color) {
        return groupsService.addGroup(name, color);
    }

    protected void setError(String message) {
        errorLabel.setText(message);
    }
}
