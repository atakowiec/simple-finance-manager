package pl.pollub.frontend.controller.group;

import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.controller.home.add.ColorListCell;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.service.AuthService;
import pl.pollub.frontend.service.ColorsService;
import pl.pollub.frontend.service.HttpService;
import pl.pollub.frontend.service.ModalService;
import pl.pollub.frontend.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Map;

public class SettingsController extends AbstractGroupController {
    @Inject
    private ColorsService colorsService;
    @Inject
    private HttpService httpService;
    @Inject
    private EventEmitter eventEmitter;
    @Inject
    private ModalService modalService;
    @Inject
    private AuthService authService;

    @FXML
    private TextField groupNameField;
    @FXML
    private ComboBox<String> colorPicker;
    @FXML
    private TextField expenseLimitField;
    @FXML
    private TextField expenseLimitRuleField;
    @FXML
    private Label nameError;
    @FXML
    private Label colorError;
    @FXML
    private Label expenseLimitError;
    @FXML
    private Label expenseLimitRuleError;
    @FXML
    private Label iconError;
    @FXML
    private Button iconPickerButton;
    @FXML
    private ImageView groupIconPreview;
    @FXML
    public Button deleteGroupButton;
    @FXML
    public Button leaveGroupButton;
    @FXML
    public Button undoButton;
    @FXML
    private Label undoStatus;

    private File selectedIconFile;

    @PostInitialize
    public void postInitialize() {
        Group group = getGroup();
        colorPicker.getItems().addAll(colorsService.getColors());

        colorPicker.setCellFactory(param -> new ColorListCell());
        colorPicker.setButtonCell(new ColorListCell(group.getColor()));

        groupNameField.setText(group.getName());

        boolean expenseLimitSet = group.getExpenseLimit() != null && group.getExpenseLimit() > 0;

        expenseLimitField.setText(expenseLimitSet ? String.valueOf(group.getExpenseLimit()) : "");
        expenseLimitRuleField.setText(group.getExpenseLimitRule() != null ? group.getExpenseLimitRule() : "");

        deleteGroupButton.setVisible(authService.getUser().getId() == group.getOwner().getId());
        deleteGroupButton.setManaged(authService.getUser().getId() == group.getOwner().getId());

        leaveGroupButton.setVisible(authService.getUser().getId() != group.getOwner().getId());
        leaveGroupButton.setManaged(authService.getUser().getId() != group.getOwner().getId());

        refreshDisplayedGroupIcon();

        // Check if undo is available
        updateUndoButtonState();
    }

    private void updateUndoButtonState() {
        boolean canUndo = groupsService.canUndo(getGroup().getId());
        undoButton.setDisable(!canUndo);
    }

    public void saveName() {
        if (groupNameField.getText().isEmpty()) {
            nameError.setText("Wpisz nazwę!");
            return;
        }

        HttpResponse<String> response = httpService.patch("/groups/" + getGroup().getId() + "/name", groupNameField.getText());

        if (response.statusCode() != 200) {
            nameError.setText("Wystąpił błąd podczas zmiany nazwy!");
            return;
        }

        JsonObject jsonResponse = JsonUtil.fromJson(response.body()).getAsJsonObject();

        getGroup().setName(jsonResponse.get("name").getAsString());
        nameError.setText("Zapisano!");
        eventEmitter.emit(EventType.GROUPS_UPDATE);
        updateUndoButtonState();
    }

    public void saveColor() {
        if (colorPicker.getValue() == null) {
            colorError.setText("Wybierz kolor!");
            return;
        }

        HttpResponse<String> response = httpService.patch("/groups/" + getGroup().getId() + "/color", colorPicker.getValue());

        if (response.statusCode() != 200) {
            colorError.setText("Wystąpił błąd podczas zmiany koloru!");
            return;
        }

        JsonObject jsonResponse = JsonUtil.fromJson(response.body()).getAsJsonObject();

        getGroup().setColor(jsonResponse.get("color").getAsString());
        colorError.setText("Zapisano!");
        eventEmitter.emit(EventType.GROUPS_UPDATE);
        updateUndoButtonState();
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
        groupIconPreview.setImage(new Image(file.toURI().toString()));
        groupIconPreview.setVisible(true);
        groupIconPreview.setManaged(true);
        iconError.setText("");
    }

    public void saveIcon() {
        if (selectedIconFile == null) {
            iconError.setText("Wybierz plik ikony!");
            return;
        }

        byte[] iconBytes = getFileBytes(selectedIconFile);
        String contentType = resolveContentType(selectedIconFile);

        if (iconBytes == null || iconBytes.length == 0) {
            iconError.setText("Nie udało się odczytać ikony!");
            return;
        }

        HttpResponse<String> response = groupsService.updateGroupIcon(getGroup().getId(), iconBytes, contentType);
        if (response.statusCode() != 200) {
            iconError.setText("Wystąpił błąd podczas zapisywania ikony!");
            return;
        }

        JsonObject jsonResponse = JsonUtil.fromJson(response.body()).getAsJsonObject();
        updateGroupIconMetadata(jsonResponse);
        groupsService.invalidateGroupIcon(getGroup().getId());
        selectedIconFile = null;
        iconPickerButton.setText("Wybierz ikonę");
        refreshDisplayedGroupIcon();
        iconError.setText("Zapisano!");
        eventEmitter.emit(EventType.GROUPS_UPDATE);
        updateUndoButtonState();
    }

    public void deleteIcon() {
        HttpResponse<String> response = groupsService.deleteGroupIcon(getGroup().getId());
        if (response.statusCode() != 200) {
            iconError.setText("Wystąpił błąd podczas usuwania ikony!");
            return;
        }

        getGroup().setHasIcon(false);
        getGroup().setIconChecksum(null);
        selectedIconFile = null;
        iconPickerButton.setText("Wybierz ikonę");
        refreshDisplayedGroupIcon();
        iconError.setText("Usunięto ikonę!");
        eventEmitter.emit(EventType.GROUPS_UPDATE);
        updateUndoButtonState();
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

            HttpResponse<String> response = httpService.patch("/groups/" + getGroup().getId() + "/expense-limit", limit);

            if (response.statusCode() != 200) {
                expenseLimitError.setText("Wystąpił błąd podczas zmiany limitu!");
                return;
            }

            JsonObject jsonResponse = JsonUtil.fromJson(response.body()).getAsJsonObject();

            getGroup().setExpenseLimit(jsonResponse.get("expenseLimit").getAsDouble());
            expenseLimitError.setText("Zapisano!");
            updateUndoButtonState();
        } catch (NumberFormatException ignored) {
            expenseLimitError.setText("Limit musi być liczbą!");
        }
    }

    public void saveExpenseLimitRule() {
        String ruleText = expenseLimitRuleField.getText() == null ? "" : expenseLimitRuleField.getText().trim();
        HttpResponse<String> response = httpService.patch("/groups/" + getGroup().getId() + "/expense-limit-rule", ruleText);

        if (response.statusCode() != 200) {
            expenseLimitRuleError.setText("Błąd zapisu reguły. Sprawdź składnię: WHEN warunek THEN akcja");
            return;
        }

        JsonObject jsonResponse = JsonUtil.fromJson(response.body()).getAsJsonObject();
        if (jsonResponse.has("expenseLimitRule") && !jsonResponse.get("expenseLimitRule").isJsonNull()) {
            getGroup().setExpenseLimitRule(jsonResponse.get("expenseLimitRule").getAsString());
        } else {
            getGroup().setExpenseLimitRule(null);
        }

        expenseLimitRuleField.setText(getGroup().getExpenseLimitRule() != null ? getGroup().getExpenseLimitRule() : "");
        expenseLimitRuleError.setText("Zapisano!");
        updateUndoButtonState();
    }

    public void handleDeleteGroup() {
        modalService.showModal("modal/remove-group-view.fxml", Map.of("groupId", groupId));
    }

    public void handleLeave() {
        modalService.showModal("modal/leave-group-view.fxml", Map.of("groupId", groupId));
    }

    public void handleUndo() {
        HttpResponse<String> response = groupsService.undoGroupChange(getGroup().getId());

        if (response.statusCode() != 200) {
            undoStatus.setText("Wystąpił błąd podczas cofania zmian!");
            return;
        }

        JsonObject jsonResponse = JsonUtil.fromJson(response.body()).getAsJsonObject();

        // Update the UI with restored values
        getGroup().setName(jsonResponse.get("name").getAsString());
        getGroup().setColor(jsonResponse.get("color").getAsString());
        getGroup().setExpenseLimit(jsonResponse.get("expenseLimit").getAsDouble());
        updateGroupIconMetadata(jsonResponse);
        groupsService.invalidateGroupIcon(getGroup().getId());

        groupNameField.setText(getGroup().getName());
        colorPicker.setButtonCell(new ColorListCell(getGroup().getColor()));

        boolean expenseLimitSet = getGroup().getExpenseLimit() != null && getGroup().getExpenseLimit() > 0;
        expenseLimitField.setText(expenseLimitSet ? String.valueOf(getGroup().getExpenseLimit()) : "");
        if (jsonResponse.has("expenseLimitRule") && !jsonResponse.get("expenseLimitRule").isJsonNull()) {
            getGroup().setExpenseLimitRule(jsonResponse.get("expenseLimitRule").getAsString());
        } else {
            getGroup().setExpenseLimitRule(null);
        }
        expenseLimitRuleField.setText(getGroup().getExpenseLimitRule() != null ? getGroup().getExpenseLimitRule() : "");
        selectedIconFile = null;
        iconPickerButton.setText("Wybierz ikonę");
        refreshDisplayedGroupIcon();

        undoStatus.setText("Cofnięto ostatnią zmianę!");
        eventEmitter.emit(EventType.GROUPS_UPDATE);

        // Update undo button state
        updateUndoButtonState();
    }

    private void refreshDisplayedGroupIcon() {
        Image image = null;

        if (selectedIconFile != null) {
            image = new Image(selectedIconFile.toURI().toString());
        } else if (getGroup().isHasIcon()) {
            image = groupsService.getGroupIconImage(getGroup());
        }

        groupIconPreview.setImage(image);
        groupIconPreview.setVisible(image != null);
        groupIconPreview.setManaged(image != null);
    }

    private void updateGroupIconMetadata(JsonObject jsonResponse) {
        boolean hasIcon = jsonResponse.has("hasIcon") && jsonResponse.get("hasIcon").getAsBoolean();
        getGroup().setHasIcon(hasIcon);

        if (jsonResponse.has("iconChecksum") && !jsonResponse.get("iconChecksum").isJsonNull()) {
            getGroup().setIconChecksum(jsonResponse.get("iconChecksum").getAsString());
        } else {
            getGroup().setIconChecksum(null);
        }
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
}
