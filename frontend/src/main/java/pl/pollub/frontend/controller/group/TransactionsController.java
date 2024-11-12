package pl.pollub.frontend.controller.group;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

    @Inject
    private TransactionService transactionService;
    @Inject
    private DependencyInjector dependencyInjector;
    @Inject
    private ModalService modalService;

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
}
