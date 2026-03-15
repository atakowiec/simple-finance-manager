package pl.pollub.frontend.controller.home.add;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.service.ColorsService;
import pl.pollub.frontend.service.GroupsService;
import pl.pollub.frontend.service.ModalService;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;

public class AddGroupController {
    @FXML
    private TextField nameInput;

    @FXML
    private ComboBox<String> colorPicker;

    @FXML
    private Label errorLabel;

    @FXML
    private Button iconPickerButton;

    @FXML
    private ImageView iconPreview;

    @Inject
    protected GroupsService groupsService;

    @Inject
    protected ColorsService colorsService;

    @Inject
    private ModalService modalService;

    @Inject
    private EventEmitter eventEmitter;

    private File selectedIconFile;

    @PostInitialize
    public void postInitialize() {
        colorPicker.getItems().addAll(colorsService.getColors());

        colorPicker.setCellFactory(param -> new ColorListCell());
        colorPicker.setButtonCell(new ColorListCell());
        iconPreview.setVisible(false);
        iconPreview.setManaged(false);
    }

    public void save() {
        setError("");

        String name = nameInput.getText();
        String color = colorPicker.getValue();

        if (name.isEmpty() || color == null || color.isEmpty()) {
            setError("Wszystkie pola są wymagane!");
            return;
        }

        byte[] iconBytes = getFileBytes(selectedIconFile);
        String iconContentType = resolveContentType(selectedIconFile);

        if (selectedIconFile != null && (iconBytes == null || iconBytes.length == 0)) {
            setError("Nie udało się odczytać wybranej ikony!");
            return;
        }

        HttpResponse<String> response = addGroup(name, color, iconBytes, iconContentType);

        if(response.statusCode() != 201) {
            setError("Wystąpił błąd podczas dodawania groupy!");
            return;
        }

        groupsService.updateGroups();

        eventEmitter.emit(EventType.GROUPS_UPDATE);

        modalService.hideModal();
    }

    public void selectIcon() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz ikonę grupy");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki obrazów", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"));

        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }

        selectedIconFile = file;
        iconPickerButton.setText(file.getName());
        iconPreview.setImage(new Image(file.toURI().toString()));
        iconPreview.setVisible(true);
        iconPreview.setManaged(true);
    }

    public void clearSelectedIcon() {
        selectedIconFile = null;
        iconPickerButton.setText("Wybierz ikonę");
        iconPreview.setImage(null);
        iconPreview.setVisible(false);
        iconPreview.setManaged(false);
    }

    private HttpResponse<String> addGroup(String name, String color, byte[] icon, String iconContentType) {
        return groupsService.addGroup(name, color, icon, iconContentType);
    }

    private byte[] getFileBytes(File file) {
        if (file == null) {
            return null;
        }

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException exception) {
            return null;
        }
    }

    private String resolveContentType(File file) {
        if (file == null) {
            return null;
        }

        try {
            String detected = Files.probeContentType(file.toPath());
            if (detected != null && !detected.isBlank()) {
                return detected;
            }
        } catch (IOException ignored) {
            // handled by extension fallback below
        }

        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".png")) {
            return "image/png";
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        if (fileName.endsWith(".webp")) {
            return "image/webp";
        }
        return null;
    }

    protected void setError(String message) {
        errorLabel.setText(message);
    }
}
