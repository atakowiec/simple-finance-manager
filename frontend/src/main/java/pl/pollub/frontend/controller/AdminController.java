package pl.pollub.frontend.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import pl.pollub.frontend.annotation.NavBar;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.Title;
import pl.pollub.frontend.annotation.View;
import pl.pollub.frontend.controller.component.ImageTableCell;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.transaction.TransactionCategory;
import pl.pollub.frontend.model.transaction.TransactionCategoryType;
import pl.pollub.frontend.service.AdminService;
import pl.pollub.frontend.user.User;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.List;


@NavBar
@Title("Panel administratora")
@View(name = "admin", path = "admin-dashboard-view.fxml")
public class AdminController {
    @Inject
    private AdminService adminService;
    @Inject
    private EventEmitter eventEmitter;

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
    private TextField userEmailField;
    @FXML
    private TextField userNameField;
    @FXML
    private ComboBox<String> userRoleField;
    @FXML
    private Label statusMessageLabel;
    @FXML
    public TextField incomeCategoryNameField;
    @FXML
    public TableColumn<TransactionCategory, String> incomeCategoryNameCol;
    @FXML
    public TableColumn<TransactionCategory, Integer> incomeCategoryIconCol;
    @FXML
    public TableView<TransactionCategory> incomeCategoriesTable;
    @FXML
    public TableColumn<TransactionCategory, String> expenseCategoryNameCol;
    @FXML
    public TableColumn<TransactionCategory, Integer> expenseCategoryIconCol;
    @FXML
    public TextField expenseCategoryNameField;
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
    @FXML
    public Button incomeCategorySelectorButton;
    @FXML
    public Button expenseCategorySelectorButton;

    private File incomeCategoryIconFile;
    private File expenseCategoryIconFile;


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

        userList = FXCollections.observableArrayList();
        usersTable.setItems(userList);

        expenseCategoryIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        expenseCategoryNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        expenseCategoryIconCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        expenseCategoryIconCol.setCellFactory(param -> new ImageTableCell());

        incomeCategoryIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        incomeCategoryNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        incomeCategoryIconCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        incomeCategoryIconCol.setCellFactory(param -> new ImageTableCell());

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
        expenseCategorySelectorButton.setText("Wybierz ikonę");
        expenseCategoryIconFile = null;
    }

    @FXML
    private void selectIncomeCategory() {
        TransactionCategory selectedCategory = incomeCategoriesTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            return;
        }
        incomeCategoryNameField.setText(selectedCategory.getName());
        incomeCategorySelectorButton.setText("Wybierz ikonę");
        incomeCategoryIconFile = null;
    }

    private void refreshExpenseCategories() {
        eventEmitter.emit(EventType.CATEGORIES_UPDATE);
        List<TransactionCategory> categories = adminService.getExpenseCategories();
        expenseCategoriesTable.getItems().clear();
        expenseCategoriesTable.getItems().addAll(categories);
    }

    private void refreshIncomeCategories() {
        eventEmitter.emit(EventType.CATEGORIES_UPDATE);
        List<TransactionCategory> categories = adminService.getIncomeCategories();
        incomeCategoriesTable.getItems().clear();
        incomeCategoriesTable.getItems().addAll(categories);
    }

    @FXML
    private void clearCategoryForm() {
        expenseCategoryNameField.clear();
        incomeCategoryNameField.clear();

        incomeCategorySelectorButton.setText("Wybierz ikonę");
        expenseCategorySelectorButton.setText("Wybierz ikonę");

        incomeCategoryIconFile = null;
        expenseCategoryIconFile = null;
    }

    @FXML
    public void addExpenseCategory() {
        String name = expenseCategoryNameField.getText();

        if (name.isEmpty() || expenseCategoryIconFile == null) {
            displayExpenseCategoryStatusMessage("Nazwa i ikona kategorii nie mogą być puste.");
            return;
        }

        TransactionCategory newCategory = new TransactionCategory();
        newCategory.setName(name);
        newCategory.setIcon(getFileBytes(expenseCategoryIconFile));
        newCategory.setCategoryType(TransactionCategoryType.EXPENSE);

        try {
            HttpResponse<String> response = adminService.addCategory(newCategory);
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

        if (name.isEmpty() || incomeCategoryIconFile == null) {
            displayIncomeCategoryStatusMessage("Nazwa i ikona kategorii nie mogą być puste.");
            return;
        }

        TransactionCategory newCategory = new TransactionCategory();
        newCategory.setName(name);
        newCategory.setIcon(getFileBytes(incomeCategoryIconFile));
        newCategory.setCategoryType(TransactionCategoryType.INCOME);

        try {
            HttpResponse<String> response = adminService.addCategory(newCategory);
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

        if (name.isEmpty()) {
            displayExpenseCategoryStatusMessage("Nazwa nie może być pusta.");
            return;
        }

        selectedCategory.setName(name);
        selectedCategory.setIcon(getFileBytes(expenseCategoryIconFile));
        selectedCategory.setCategoryType(TransactionCategoryType.EXPENSE);

        try {
            HttpResponse<String> response = adminService.updateCategory(selectedCategory);
            if (response.statusCode() == 200) {
                displayExpenseCategoryStatusMessage("Kategoria wydatków została zaktualizowana.");
                clearCategoryForm();
                refreshExpenseCategories();
            } else {
                displayExpenseCategoryStatusMessage("Błąd podczas aktualizacji kategorii wydatków.");
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        if (name.isEmpty()) {
            displayIncomeCategoryStatusMessage("Nazwa nie może być pusta.");
            return;
        }

        selectedCategory.setName(name);
        selectedCategory.setIcon(getFileBytes(incomeCategoryIconFile));
        selectedCategory.setCategoryType(TransactionCategoryType.INCOME);

        try {
            HttpResponse<String> response = adminService.updateCategory(selectedCategory);
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
            HttpResponse<String> response = adminService.deleteCategory((long) selectedCategory.getId());
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
            HttpResponse<String> response = adminService.deleteCategory((long) selectedCategory.getId());
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

    private File getImage() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        fileChooser.setTitle("Wybierz ikonę kategorii przychodów");

        // open
        return fileChooser.showOpenDialog(null);
    }

    private byte[] getFileBytes(File file) {
        if(file == null) {
            return null;
        }

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void openIncomeCategorySelector() {
        File file = getImage();

        incomeCategoryIconFile = file;
        if (file != null) {
            incomeCategorySelectorButton.setText(file.getName());
        } else {
            incomeCategorySelectorButton.setText("Wybierz ikonę");
        }
    }

    public void openExpenseCategorySelector() {
        File file = getImage();

        expenseCategoryIconFile = file;
        if (file != null) {
            expenseCategorySelectorButton.setText(file.getName());
        } else {
            expenseCategorySelectorButton.setText("Wybierz ikonę");
        }
    }
}

