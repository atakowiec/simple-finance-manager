package pl.pollub.frontend.controller.group;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.controller.group.transaction.TransactionListCell;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.model.transaction.Expense;
import pl.pollub.frontend.model.transaction.Income;
import pl.pollub.frontend.model.transaction.Transaction;
import pl.pollub.frontend.service.ModalService;
import pl.pollub.frontend.service.TransactionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TransactionsController extends AbstractGroupController {
    @FXML
    private ListView<Transaction> mainList;
    @FXML
    public Label expensesTotalLabel;
    @FXML
    public Label incomesTotalLabel;
    @FXML
    private TextField queryField;
    @FXML
    private Label queryHelpLabel;

    @Inject
    private TransactionService transactionService;
    @Inject
    private DependencyInjector dependencyInjector;
    @Inject
    private ModalService modalService;

    private boolean isQueryActive = false;

    @PostInitialize
    public void postInitialize() {
        onTransactionUpdate();

        mainList.setCellFactory(param -> {
            TransactionListCell transactionListCell = new TransactionListCell();
            dependencyInjector.manualInject(transactionListCell);
            dependencyInjector.runPostInitialize(transactionListCell);
            return transactionListCell;
        });
    }

    @OnEvent(EventType.TRANSACTION_UPDATE)
    public void onTransactionUpdate() {
        Group group = getGroup();

        List<Expense> expenses = transactionService.fetchExpenses(group.getId());
        List<Income> incomes = transactionService.fetchIncomes(group.getId());

        mainList.getItems().clear();
        mainList.getItems().addAll(expenses);
        mainList.getItems().addAll(incomes);

        mainList.getItems().sort((t1, t2) -> t2.getLocalDate().compareTo(t1.getLocalDate()));

        String formattedExpensesTotal = String.format("%.2f", getSumThisMonth(expenses));
        String formattedIncomesTotal = String.format("%.2f", getSumThisMonth(incomes));

        expensesTotalLabel.setText(formattedExpensesTotal + " zł");
        incomesTotalLabel.setText(formattedIncomesTotal + " zł");
    }

    private double getSumThisMonth(List<? extends Transaction> transactions) {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);

        return transactions.stream()
                .filter(t -> t.getLocalDate().isEqual(startDate) || t.getLocalDate().isAfter(startDate))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public void openImport() {
        modalService.showModal("modal/import-export-view.fxml", Map.of("type", "import", "groupId", getGroup().getId()));
    }

    public void openExport() {
        modalService.showModal("modal/import-export-view.fxml", Map.of("type", "export", "groupId", getGroup().getId()));
    }

    @FXML
    public void executeQuery() {
        String query = queryField.getText();
        if (query == null || query.trim().isEmpty()) {
            queryHelpLabel.setText("Proszę wpisać zapytanie");
            queryHelpLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: red;");
            return;
        }

        try {
            Group group = getGroup();
            List<Expense> expenses = transactionService.queryExpenses(group.getId(), query);
            List<Income> incomes = transactionService.queryIncomes(group.getId(), query);

            mainList.getItems().clear();
            mainList.getItems().addAll(expenses);
            mainList.getItems().addAll(incomes);

            mainList.getItems().sort((t1, t2) -> t2.getLocalDate().compareTo(t1.getLocalDate()));

            String formattedExpensesTotal = String.format("%.2f", expenses.stream().mapToDouble(Transaction::getAmount).sum());
            String formattedIncomesTotal = String.format("%.2f", incomes.stream().mapToDouble(Transaction::getAmount).sum());

            expensesTotalLabel.setText(formattedExpensesTotal + " zł");
            incomesTotalLabel.setText(formattedIncomesTotal + " zł");

            queryHelpLabel.setText("Znaleziono: " + expenses.size() + " wydatków i " + incomes.size() + " przychodów");
            queryHelpLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: green;");
            isQueryActive = true;

        } catch (Exception e) {
            queryHelpLabel.setText("Błąd zapytania: " + e.getMessage());
            queryHelpLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: red;");
        }
    }

    @FXML
    public void clearQuery() {
        queryField.clear();
        queryHelpLabel.setText("Przykłady: amount > 100 | category = 'Food' | name contains 'coffee' | date after 2026-01-01");
        queryHelpLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        isQueryActive = false;
        onTransactionUpdate();
    }
}
