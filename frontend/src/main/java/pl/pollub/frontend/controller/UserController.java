package pl.pollub.frontend.controller;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import pl.pollub.frontend.annotation.NavBar;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.View;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.service.AuthService;
import pl.pollub.frontend.service.UserService;
import pl.pollub.frontend.util.JsonUtil;

import java.net.http.HttpResponse;

@NavBar
@View(name = "user", path = "user-profile-view.fxml")
public class UserController {

    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;

    @FXML
    public Label userNameLabel;
    @FXML
    private VBox editUsernamePane;
    @FXML
    private VBox editEmailPane;
    @FXML
    private VBox editPasswordPane;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Label statusMessageLabel;

    @PostInitialize
    private void postInitialize() {
        userNameLabel.setText(authService.getUser().getUsername());
    }

    @FXML
    private void switchToUsernameEdit() {
        setVisiblePane(editUsernamePane);
        clearFields(usernameField);
    }

    @FXML
    private void switchToEmailEdit() {
        setVisiblePane(editEmailPane);
        clearFields(emailField);
    }

    @FXML
    private void switchToPasswordEdit() {
        setVisiblePane(editPasswordPane);
        clearFields(oldPasswordField, newPasswordField);
    }

    @FXML
    private void saveUsernameChanges() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            displayStatusMessage("Nazwa użytkownika nie może być pusta.");
            return;
        }

        if (username.length() < 3 || username.length() > 20) {
            displayStatusMessage("Nazwa użytkownika musi mieć od 3 do 20 znaków.");
            return;
        }

        HttpResponse<String> response = userService.updateUsername(username);

        if (response.statusCode() == 200) {
            displayStatusMessage("Nazwa użytkownika została zaktualizowana.");
            userNameLabel.setText(username);
            return;
        }

        if (response.statusCode() == 409) {
            displayStatusMessage("Nazwa użytkownika jest już zajęta.");
            return;
        }

        if (response.statusCode() == 404) {
            displayStatusMessage("Użytkownik nie został znaleziony.");
            return;
        }

        displayStatusMessage("Wystąpił błąd podczas aktualizacji nazwy użytkownika.");
    }

    @FXML
    private void saveEmailChanges() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            displayStatusMessage("Email nie może być pusty.");
            return;
        }

        if (!isValidEmail(email)) {
            displayStatusMessage("Wprowadź poprawny adres e-mail.");
            return;
        }

        HttpResponse<String> response = userService.updateEmail(email);

        if (response.statusCode() == 200) {
            displayStatusMessage("Zaktualizowano adres e-mail.");
            return;
        }

        if (response.statusCode() == 409) {
            displayStatusMessage("Ten adres e-mail jest już zajęty.");
            return;
        }

        JsonObject json = JsonUtil.fromJson(response.body()).getAsJsonObject();
        if (json.has("message") && "Validation failed".equals(json.get("message").getAsString())) {
            JsonObject errors = json.getAsJsonObject("errors");
            if (errors.has("email")) {
                String emailError = errors.getAsJsonArray("email").get(0).getAsString();
                displayStatusMessage(emailError);
                return;
            }
        }

        displayStatusMessage("Wystąpił błąd związany ze zmianą adresu e-mail.");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    @FXML
    private void savePasswordChanges() {
        String oldPassword = oldPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            displayStatusMessage("Obydwa pola muszą być wypełnione.");
            return;
        }

        if (newPassword.length() < 6 || newPassword.length() > 30) {
            displayStatusMessage("Nowe hasło musi mieć od 6 do 30 znaków.");
            return;
        }

        HttpResponse<String> response = userService.updateUserPassword(oldPassword, newPassword);

        if (response.statusCode() == 200) {
            displayStatusMessage("Zmieniono hasło.");
            return;
        }

        if (response.statusCode() == 401) {
            displayStatusMessage("Stare hasło jest nieprawidłowe.");
            return;
        }

        JsonObject json = JsonUtil.fromJson(response.body()).getAsJsonObject();
        if (json.has("message") && "Validation failed".equals(json.get("message").getAsString())) {
            JsonObject errors = json.getAsJsonObject("errors");
            if (errors.has("oldPassword")) {
                String oldPasswordError = errors.getAsJsonArray("oldPassword").get(0).getAsString();
                displayStatusMessage(oldPasswordError);
                return;
            }
            if (errors.has("newPassword")) {
                String newPasswordError = errors.getAsJsonArray("newPassword").get(0).getAsString();
                displayStatusMessage(newPasswordError);
                return;
            }
        }

        displayStatusMessage("Wystąpił błąd podczas zmiany hasła");
    }

    private void setVisiblePane(VBox pane) {
        editUsernamePane.setVisible(false);
        editEmailPane.setVisible(false);
        editPasswordPane.setVisible(false);

        pane.setVisible(true);
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void displayStatusMessage(String message) {
        statusMessageLabel.setText(message);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> statusMessageLabel.setText("")));
        timeline.play();
    }
}
