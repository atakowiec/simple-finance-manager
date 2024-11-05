package pl.pollub.frontend.controller;

import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pl.pollub.frontend.annotation.Title;
import pl.pollub.frontend.annotation.View;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.service.AuthService;
import pl.pollub.frontend.service.HttpService;
import pl.pollub.frontend.service.ScreenService;
import pl.pollub.frontend.user.User;
import pl.pollub.frontend.util.JsonUtil;
import pl.pollub.frontend.util.SimpleJsonBuilder;

import java.net.http.HttpResponse;

@Title("Logowanie")
@View(name = "login", path = "login-view.fxml")
public class LoginController {

    @Inject
    public ScreenService screenService;

    @Inject
    public HttpService httpService;

    @Inject
    public AuthService authService;

    @FXML
    public TextField identifierField;
    @FXML
    public PasswordField passwordField;

    @FXML
    public Label identifierError;
    @FXML
    public Label passwordError;
    @FXML
    public Label formError;

    @FXML
    public void initialize() {
        identifierError.setVisible(false);
        passwordError.setVisible(false);
        formError.setVisible(false);
    }

    @FXML
    public void login() {
        if (!validateForm()) {
            return;
        }

        HttpResponse<String> response = httpService.post("/auth/login", SimpleJsonBuilder.empty()
                .add("identifier", identifierField.getText())
                .add("password", passwordField.getText())
                .build());

        handleResponse(response);
    }

    private boolean validateForm() {
        identifierError.setVisible(false);
        passwordError.setVisible(false);
        formError.setVisible(false);

        String identifier = identifierField.getText();
        String password = passwordField.getText();

        boolean valid = true;

        if (identifier.isEmpty()) {
            identifierError.setText("E-mail lub nazwa użytkownika nie może być pusta.");
            identifierError.setVisible(true);
            valid = false;
        }

        if (password.isEmpty()) {
            passwordError.setText("Hasło nie może być puste.");
            passwordError.setVisible(true);
            valid = false;
        }

        return valid;
    }

    private void handleResponse(HttpResponse<String> responseObj) {

        JsonObject response = JsonUtil.fromJson(responseObj.body()).getAsJsonObject();

        if (responseObj.statusCode() == 401) {
            formError.setText("Nieprawidłowa nazwa użytkownika/email lub hasło.");
            formError.setVisible(true);
            return;
        }

        if (responseObj.statusCode() != 200) {
            formError.setText("Wystąpił błąd podczas logowania.");
            formError.setVisible(true);
            return;
        }

        handleValidLogin(response);
    }

    private void handleValidLogin(JsonObject response) {
        if (!response.has("token") || response.get("token").isJsonNull() || response.get("token").getAsString().isEmpty()) {
            formError.setText("Wystąpił błąd podczas logowania (brak tokenu).");
            formError.setVisible(true);
            return;
        }

        String token = response.get("token").getAsString();
        String email = response.has("email") ? response.get("email").getAsString() : "";

        User user = new User();
        user.setId(response.get("id").getAsLong());
        user.setUsername(response.get("username").getAsString());
        user.setAdmin(response.get("role").getAsString().equals("ADMIN"));
        user.setEmail(email);
        user.setToken(token);

        authService.setUser(user);

        screenService.switchTo("home");
    }

    public void goToRegister() {
        screenService.switchTo("register");
    }

}
