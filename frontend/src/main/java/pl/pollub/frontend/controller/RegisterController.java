package pl.pollub.frontend.controller;

import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pl.pollub.frontend.manager.AuthManager;
import pl.pollub.frontend.manager.HttpManager;
import pl.pollub.frontend.manager.ScreenManager;
import pl.pollub.frontend.user.User;
import pl.pollub.frontend.util.JsonUtil;
import pl.pollub.frontend.util.SimpleJsonBuilder;

import java.net.http.HttpResponse;
import java.util.Objects;

public class RegisterController {
    public ScreenManager screenManager;

    public HttpManager httpManager;

    public AuthManager authManager;

    @FXML
    public TextField usernameField;
    @FXML
    public TextField emailField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField passwordRepeatField;

    @FXML
    public Label usernameError;
    @FXML
    public Label emailError;
    @FXML
    public Label passwordError;
    @FXML
    public Label passwordRepeatError;
    @FXML
    public Label formError;

    public void register() {
        if (!validateForm()) {
            return;
        }

        HttpResponse<String> response = httpManager.post("/auth/register", SimpleJsonBuilder.empty()
                .add("username", usernameField.getText())
                .add("email", emailField.getText())
                .add("password", passwordField.getText())
                .build());

        handleResponse(response);
    }

    private boolean validateForm() {
        usernameError.setVisible(false);
        emailError.setVisible(false);
        passwordError.setVisible(false);
        passwordRepeatError.setVisible(false);
        formError.setVisible(false);

        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String passwordRepeat = passwordRepeatField.getText();

        usernameError.setVisible(false);
        emailError.setVisible(false);
        passwordError.setVisible(false);
        passwordRepeatError.setVisible(false);

        if (username.isEmpty()) {
            usernameError.setText("Nazwa użytkownika nie może być pusta");
            usernameError.setVisible(true);
        }

        if (email.isEmpty()) {
            emailError.setText("Email nie może być pusty");
            emailError.setVisible(true);
        }

        if (password.isEmpty()) {
            passwordError.setText("Hasło nie może być puste");
            passwordError.setVisible(true);
        }

        if (passwordRepeat.isEmpty()) {
            passwordRepeatError.setText("Powtórz hasło");
            passwordRepeatError.setVisible(true);
        }

        if (!password.equals(passwordRepeat)) {
            passwordRepeatError.setText("Hasła nie są takie same");
            passwordRepeatError.setVisible(true);
        }

        return !usernameError.isVisible() && !emailError.isVisible() && !passwordError.isVisible() && !passwordRepeatError.isVisible();
    }

    private void handleResponse(HttpResponse<String> responseObj) {
        JsonObject response = JsonUtil.fromJson(responseObj.body()).getAsJsonObject();
        if (responseObj.statusCode() == 400) {

            if (Objects.equals(response.get("message").getAsString(), "Validation failed")) {
                handleValidationErrors(response.get("errors").getAsJsonObject());
                return;
            }
        }

        if (responseObj.statusCode() == 409) {
            String message = response.get("message").getAsString();

            if (Objects.equals(message, "username")) {
                usernameError.setText("Nazwa użytkownika jest zajęta");
                usernameError.setVisible(true);
                return;
            }

            if (Objects.equals(message, "email")) {
                emailError.setText("Email jest zajęty");
                emailError.setVisible(true);
                return;
            }

            return;
        }

        if (responseObj.statusCode() != 201) {
            formError.setText("Wystąpił błąd podczas rejestracji");
            formError.setVisible(true);
            return;
        }

        handleValidLogin(response);
    }

    private void handleValidLogin(JsonObject response) {
        if (!response.has("token") || response.get("token").isJsonNull() || response.get("token").getAsString().isEmpty()) {
            formError.setText("Wystąpił błąd podczas rejestracji (brak tokenu)");
            formError.setVisible(true);
            return;
        }

        String token = response.get("token").getAsString();

        User user = new User();
        user.setId(response.get("id").getAsLong());
        user.setUsername(response.get("username").getAsString());
        user.setAdmin(response.get("role").getAsString().equals("ADMIN"));
        user.setEmail(response.get("email").getAsString());
        user.setToken(token);

        authManager.setUser(user);

        screenManager.switchTo("home");
    }

    private void handleValidationErrors(JsonObject errors) {
        if (errors.has("username")) {
            usernameError.setText(errors.get("username").getAsJsonArray().get(0).getAsString());
            usernameError.setVisible(true);
        }

        if (errors.has("email")) {
            emailError.setText(errors.get("email").getAsJsonArray().get(0).getAsString());
            emailError.setVisible(true);
        }

        if (errors.has("password")) {
            passwordError.setText(errors.get("password").getAsJsonArray().get(0).getAsString());
            passwordError.setVisible(true);
        }
    }
}
