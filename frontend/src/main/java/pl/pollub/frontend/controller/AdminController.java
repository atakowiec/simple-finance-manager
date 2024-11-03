package pl.pollub.frontend.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import pl.pollub.frontend.annotation.NavBar;
import pl.pollub.frontend.annotation.Title;
import pl.pollub.frontend.annotation.View;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.transaction.TransactionCategory;
import pl.pollub.frontend.service.AdminService;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.user.User;
import java.net.http.HttpResponse;
import java.util.List;


@NavBar
@Title("Panel administratora")
@View(name = "admin", path = "admin-dashboard-view.fxml")
public class AdminController {


    @Inject
    private AdminService adminService;

    @FXML
    private AnchorPane usersPane;
    @FXML
    private AnchorPane categoriesPane;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Long> userIdCol;
    @FXML
    private TableColumn<User, String> userRoleCol;
    @FXML
    private TableColumn<User, String> userEmailCol;
    @FXML
    private TableColumn<User, String> userNameCol;
    @FXML
    private TableColumn<User, Double> userLimitCol;
    @FXML
    private TextField userEmailField;
    @FXML
    private TextField userNameField;
    @FXML
    private ComboBox<String> userRoleField;
    @FXML
    private TextField userLimitField;
    @FXML
    private Label statusMessageLabel;
    @FXML
    public TextField incomeCategoryNameField;
    @FXML
    public TextField incomeCategoryIconField;
    @FXML
    public TableColumn<TransactionCategory, String> incomeCategoryNameCol;
    @FXML
    public TableColumn<TransactionCategory, String> incomeCategoryIconCol;
    @FXML
    public TableView<TransactionCategory> incomeCategoriesTable;
    @FXML
    public TableColumn<TransactionCategory, String> expenseCategoryNameCol;
    @FXML
    public TableColumn<TransactionCategory, String> expenseCategoryIconCol;
    @FXML
    public TextField expenseCategoryNameField;
    @FXML
    public TextField expenseCategoryIconField;
    @FXML
    public TableView<TransactionCategory> expenseCategoriesTable;
    @FXML
    public TableColumn<TransactionCategory, String> expenseCategoryIdCol;
    @FXML
    public TableColumn<TransactionCategory, String> incomeCategoryIdCol;
    @FXML
    private Label expenseCategoryStatusMessageLabel;
    @FXML
    private Label incomeCategoryStatusMessageLabel;


    private ObservableList<User> userList;
    private ObservableList<TransactionCategory> expenseCategoryList;
    private ObservableList<TransactionCategory> incomeCategoryList;


    @FXML
    public void initialize() {
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        userRoleCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new SimpleStringProperty(user.getRole());
        });

        userLimitCol.setCellValueFactory(new PropertyValueFactory<>("monthlyLimit"));

        userList = FXCollections.observableArrayList();
        usersTable.setItems(userList);

        expenseCategoryIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        expenseCategoryNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        expenseCategoryIconCol.setCellValueFactory(new PropertyValueFactory<>("icon"));

        incomeCategoryIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        incomeCategoryNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        incomeCategoryIconCol.setCellValueFactory(new PropertyValueFactory<>("icon"));

        expenseCategoryList = FXCollections.observableArrayList();
        incomeCategoryList = FXCollections.observableArrayList();

        expenseCategoriesTable.setItems(expenseCategoryList);
        incomeCategoriesTable.setItems(incomeCategoryList);

        expenseCategoriesTable.setOnMouseClicked(event -> selectExpenseCategory());

        incomeCategoriesTable.setOnMouseClicked(event -> selectIncomeCategory());

    }

    @PostInitialize
    public void postInitialize() {
        loadUsersFromDatabase();
        loadExpenseCategories();
        loadIncomeCategories();
    }

    private void loadUsersFromDatabase() {
        if (adminService == null) {
            showErrorDialog("Błąd", "Usługa administratora nie jest dostępna.");
        } else {
            try {
                List<User> users = adminService.getAllUsers();
                userList.setAll(users);
            } catch (RuntimeException e) {
                showErrorDialog("Błąd", "Nie udało się załadować użytkowników: " + e.getMessage());
            } catch (Exception e) {
                showErrorDialog("Błąd", "Wystąpił nieoczekiwany błąd: " + e.getMessage());
            }
        }
    }


    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void showUsersPane() {
        usersPane.setVisible(true);
        categoriesPane.setVisible(false);
    }

    @FXML
    private void showCategoriesPane() {
        categoriesPane.setVisible(true);
        usersPane.setVisible(false);
    }

    @FXML
    private void selectUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            return;
        }
        userEmailField.setText(selectedUser.getEmail());
        userNameField.setText(selectedUser.getUsername());
        userRoleField.getSelectionModel().select(selectedUser.getRole());
        userLimitField.setText(selectedUser.getMonthlyLimit() != null ? String.valueOf(selectedUser.getMonthlyLimit()) : "");
    }


    @FXML
    private void updateUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            displayStatusMessage("Proszę zaznaczyć użytkownika do aktualizacji.");
        } else {
            try {
                HttpResponse<String> response;
                String email = userEmailField.getText().trim();
                String username = userNameField.getText().trim();
                String role = userRoleField.getSelectionModel().getSelectedItem();
                String limitText = userLimitField.getText().trim();

                if (!email.isEmpty() && !email.equals(selectedUser.getEmail())) {
                    if (!isValidEmail(email)) {
                        displayStatusMessage("Wprowadź poprawny adres e-mail.");
                        return;
                    }
                    response = adminService.updateEmail(selectedUser.getId(), email);
                    if (response.statusCode() == 400) {
                        displayStatusMessage("Wprowadź poprawny adres e-mail.");
                        return;
                    }
                    if (response.statusCode() == 409) {
                        displayStatusMessage("Ten adres e-mail jest już zajęty.");
                        return;
                    }
                }

                if (username.isEmpty()) {
                    displayStatusMessage("Nazwa użytkownika nie może być pusta.");
                    return;
                }

                if (username.length() < 3 || username.length() > 20) {
                    displayStatusMessage("Nazwa użytkownika musi mieć od 3 do 20 znaków.");
                    return;
                }

                if (!username.equals(selectedUser.getUsername())) {
                    response = adminService.updateUsername(selectedUser.getId(), username);
                    if (response.statusCode() == 200) {
                        displayStatusMessage("Nazwa użytkownika została zaktualizowana.");
                        clearUserForm();
                        loadUsersFromDatabase();
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
                }

                if (role != null && !role.equals(selectedUser.getRole())) {
                    response = adminService.updateRole(selectedUser.getId(), role);
                    if (response.statusCode() == 400) {
                        displayStatusMessage("Wprowadź poprawną rolę użytkownika.");
                        return;
                    }
                    if (response.statusCode() != 200) {
                        displayStatusMessage("Błąd podczas aktualizacji roli użytkownika.");
                        return;
                    }
                }

                if (!limitText.isEmpty() && !limitText.equals(String.valueOf(selectedUser.getMonthlyLimit()))) {
                    try {
                        double limit = Double.parseDouble(limitText);
                        if (limit <= 0) {
                            displayStatusMessage("Limit musi być większy od zera.");
                            return;
                        }

                        response = adminService.updateLimit(selectedUser.getId(), limit);
                        if (response.statusCode() == 400) {
                            displayStatusMessage("Wprowadź poprawny limit wydatków.");
                            return;
                        }
                        if (response.statusCode() != 200) {
                            displayStatusMessage("Błąd podczas aktualizacji limitu wydatków.");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        displayStatusMessage("Wprowadź poprawną liczbę dla limitu wydatków.");
                        return;
                    }
                }

                displayStatusMessage("Dane użytkownika zostały pomyślnie zaktualizowane.");
                clearUserForm();
                loadUsersFromDatabase();

            } catch (Exception e) {
                displayStatusMessage("Wystąpił błąd podczas aktualizacji użytkownika: " + e.getMessage());
            }
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }


    @FXML
    private void deleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            displayStatusMessage("Proszę zaznaczyć użytkownika do usunięcia.");
        } else {
            try {
                HttpResponse<String> response = adminService.deleteUser(selectedUser.getId());
                if (response.statusCode() == 200) {
                    displayStatusMessage("Użytkownik został pomyślnie usunięty.");
                    clearUserForm();
                    loadUsersFromDatabase();
                } else if (response.statusCode() == 404) {
                    displayStatusMessage("Użytkownik nie został znaleziony.");
                } else {
                    displayStatusMessage("Błąd podczas usuwania użytkownika.");
                }
            } catch (Exception e) {
                displayStatusMessage("Wystąpił błąd podczas usuwania użytkownika: " + e.getMessage());
            }
        }
    }


    @FXML
    private void clearUserForm() {
        userEmailField.clear();
        userNameField.clear();
        userRoleField.getSelectionModel().clearSelection();
        userLimitField.clear();
    }


    private void loadExpenseCategories() {
        if (adminService == null) {
            displayExpenseCategoryStatusMessage("Usługa administratora nie jest dostępna.");
        } else {
            try {
                List<TransactionCategory> expenseCategories = adminService.getExpenseCategories();
                expenseCategoryList.setAll(expenseCategories);
            } catch (RuntimeException e) {
                displayExpenseCategoryStatusMessage("Błąd: " + e.getMessage());
            } catch (Exception e) {
                displayExpenseCategoryStatusMessage("Wystąpił nieoczekiwany błąd: " + e.getMessage());
            }
        }
    }


    private void loadIncomeCategories() {
        if (adminService == null) {
            displayIncomeCategoryStatusMessage("Usługa administratora nie jest dostępna.");
        } else {
            try {
                List<TransactionCategory> incomeCategories = adminService.getIncomeCategories();
                incomeCategoryList.setAll(incomeCategories);
            } catch (RuntimeException e) {
                displayIncomeCategoryStatusMessage("Błąd: " + e.getMessage());
            } catch (Exception e) {
                displayIncomeCategoryStatusMessage("Wystąpił nieoczekiwany błąd: " + e.getMessage());
            }
        }
    }

    @FXML
    private void selectExpenseCategory() {
        TransactionCategory selectedCategory = expenseCategoriesTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            return;
        }
        expenseCategoryNameField.setText(selectedCategory.getName());
        //todo handle icon
    }

    @FXML
    private void selectIncomeCategory() {
        TransactionCategory selectedCategory = incomeCategoriesTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            return;
        }
        incomeCategoryNameField.setText(selectedCategory.getName());
        //todo handle icon
    }

    private void refreshExpenseCategories() {
        List<TransactionCategory> categories = adminService.getExpenseCategories();
        expenseCategoriesTable.getItems().clear();
        expenseCategoriesTable.getItems().addAll(categories);
    }

    private void refreshIncomeCategories() {
        List<TransactionCategory> categories = adminService.getIncomeCategories();
        incomeCategoriesTable.getItems().clear();
        incomeCategoriesTable.getItems().addAll(categories);
    }

    @FXML
    private void clearCategoryForm() {
        expenseCategoryNameField.clear();
        expenseCategoryIconField.clear();
        incomeCategoryNameField.clear();
        incomeCategoryIconField.clear();
    }

    @FXML
    public void addExpenseCategory() {
        String name = expenseCategoryNameField.getText();
        String icon = expenseCategoryIconField.getText();

        if (name.isEmpty() || icon.isEmpty()) {
            displayExpenseCategoryStatusMessage("Nazwa i ikona kategorii nie mogą być puste.");
            return;
        }

        TransactionCategory newCategory = new TransactionCategory();
        newCategory.setName(name);
        newCategory.setIcon(icon);

        try {
            HttpResponse<String> response = adminService.addExpenseCategory(newCategory);
            if (response.statusCode() == 200) {
                displayExpenseCategoryStatusMessage("Kategoria wydatków została dodana.");
                clearCategoryForm();
                refreshExpenseCategories();
            } else if (response.statusCode() == 409) {
                displayExpenseCategoryStatusMessage("Nazwa kategorii jest zajęta.");
                refreshExpenseCategories();
            } else {
                displayExpenseCategoryStatusMessage("Błąd podczas dodawania kategorii.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            displayExpenseCategoryStatusMessage("Wystąpił błąd: " + e.getMessage());
        }
    }

    @FXML
    public void addIncomeCategory() {
        String name = incomeCategoryNameField.getText();
        String icon = incomeCategoryIconField.getText();

        if (name.isEmpty() || icon.isEmpty()) {
            displayIncomeCategoryStatusMessage("Nazwa i ikona kategorii nie mogą być puste.");
            return;
        }

        TransactionCategory newCategory = new TransactionCategory();
        newCategory.setName(name);
        newCategory.setIcon(icon);

        try {
            HttpResponse<String> response = adminService.addIncomeCategory(newCategory);
            if (response.statusCode() == 200) {
                displayIncomeCategoryStatusMessage("Kategoria wydatków została dodana.");
                clearCategoryForm();
                refreshIncomeCategories();
            } else if (response.statusCode() == 409) {
                displayIncomeCategoryStatusMessage("Nazwa kategorii jest zajęta.");
                refreshIncomeCategories();
            } else {
                displayIncomeCategoryStatusMessage("Błąd podczas dodawania kategorii.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            displayIncomeCategoryStatusMessage("Wystąpił błąd: " + e.getMessage());
        }
    }


    @FXML
    private void updateExpenseCategory() {
        TransactionCategory selectedCategory = expenseCategoriesTable.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            displayExpenseCategoryStatusMessage("Proszę wybrać kategorię wydatków do aktualizacji.");
            return;
        }

        String name = expenseCategoryNameField.getText().trim();
        String icon = expenseCategoryIconField.getText().trim();

        if (name.isEmpty() || icon.isEmpty()) {
            displayExpenseCategoryStatusMessage("Nazwa i ikona kategorii nie mogą być puste.");
            return;
        }

        selectedCategory.setName(name);
        selectedCategory.setIcon(icon);

        try {
            HttpResponse<String> response = adminService.updateExpenseCategory(selectedCategory);
            if (response.statusCode() == 200) {
                displayExpenseCategoryStatusMessage("Kategoria wydatków została zaktualizowana.");
                clearCategoryForm();
                refreshExpenseCategories();
            } else {
                displayExpenseCategoryStatusMessage("Błąd podczas aktualizacji kategorii wydatków.");
            }
        } catch (Exception e) {
            displayExpenseCategoryStatusMessage("Wystąpił błąd: " + e.getMessage());
        }
    }


    @FXML
    private void updateIncomeCategory() {
        TransactionCategory selectedCategory = incomeCategoriesTable.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            displayIncomeCategoryStatusMessage("Proszę wybrać kategorię przychodów do aktualizacji.");
            return;
        }

        String name = incomeCategoryNameField.getText().trim();
        String icon = incomeCategoryIconField.getText().trim();

        if (name.isEmpty() || icon.isEmpty()) {
            displayIncomeCategoryStatusMessage("Nazwa i ikona kategorii nie mogą być puste.");
            return;
        }

        selectedCategory.setName(name);
        selectedCategory.setIcon(icon);

        try {
            HttpResponse<String> response = adminService.updateIncomeCategory(selectedCategory);
            if (response.statusCode() == 200) {
                displayIncomeCategoryStatusMessage("Kategoria przychodów została zaktualizowana.");
                clearCategoryForm();
                refreshIncomeCategories();
            } else {
                displayIncomeCategoryStatusMessage("Błąd podczas aktualizacji kategorii przychodów.");
            }
        } catch (Exception e) {
            displayIncomeCategoryStatusMessage("Wystąpił błąd: " + e.getMessage());
        }
    }


    public void deleteExpenseCategory() {
        TransactionCategory selectedCategory = expenseCategoriesTable.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            displayExpenseCategoryStatusMessage("Proszę wybrać kategorię wydatków do usunięcia.");
            return;
        }

        try {
            HttpResponse<String> response = adminService.deleteExpenseCategory((long) selectedCategory.getId());
            if (response.statusCode() == 200) {
                displayExpenseCategoryStatusMessage("Kategoria wydatków została usunięta.");
                clearCategoryForm();
                refreshExpenseCategories();
            } else {
                displayExpenseCategoryStatusMessage("Błąd podczas usuwania kategorii wydatków.");
            }
        } catch (Exception e) {
            displayExpenseCategoryStatusMessage("Wystąpił błąd: " + e.getMessage());
        }
    }

    public void deleteIncomeCategory() {
        TransactionCategory selectedCategory = incomeCategoriesTable.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            displayIncomeCategoryStatusMessage("Proszę wybrać kategorię przychodów do usunięcia.");
            return;
        }

        try {
            HttpResponse<String> response = adminService.deleteIncomeCategory((long) selectedCategory.getId());
            if (response.statusCode() == 200) {
                displayIncomeCategoryStatusMessage("Kategoria przychodów została usunięta.");
                clearCategoryForm();
                refreshIncomeCategories();
            } else {
                displayIncomeCategoryStatusMessage("Błąd podczas usuwania kategorii przychodów.");
            }
        } catch (Exception e) {
            displayIncomeCategoryStatusMessage("Wystąpił błąd: " + e.getMessage());
        }
    }

    private void displayStatusMessage(String message) {
        statusMessageLabel.setText(message);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> statusMessageLabel.setText("")));
        timeline.play();
    }

    private void displayIncomeCategoryStatusMessage(String message) {
        incomeCategoryStatusMessageLabel.setText(message);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> incomeCategoryStatusMessageLabel.setText("")));
        timeline.play();
    }

    private void displayExpenseCategoryStatusMessage(String message) {
        expenseCategoryStatusMessageLabel.setText(message);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> expenseCategoryStatusMessageLabel.setText("")));
        timeline.play();
    }

}

