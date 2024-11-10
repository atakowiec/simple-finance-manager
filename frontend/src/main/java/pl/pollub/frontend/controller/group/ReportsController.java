package pl.pollub.frontend.controller.group;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.GroupStatEntry;
import pl.pollub.frontend.service.GroupsService;

import java.util.List;

public class ReportsController extends AbstractGroupController {

    @Inject
    private GroupsService groupsService;

    @FXML
    private LineChart<String, Number> incomeExpenseLineChart;

    @FXML
    private BarChart<String, Number> categoryExpenseBarChart;

    public ReportsController() {
        this.groupsService = new GroupsService();
    }

    @PostInitialize
    public void postInitialize() {
        loadLastMonthStats();
    }

    private void loadLastMonthStats() {
        List<GroupStatEntry> incomes = groupsService.fetchLastMonthIncomeStats(groupId);
        List<GroupStatEntry> expenses = groupsService.fetchLastMonthExpenseStats(groupId);
        List<GroupStatEntry> expenseByCategory = groupsService.fetchLastMonthCategoryExpenseStats(groupId);

        populateIncomeExpenseLineChart(incomes, expenses);
        populateCategoryExpenseBarChart(expenseByCategory);
    }

    private void populateIncomeExpenseLineChart(List<GroupStatEntry> incomes, List<GroupStatEntry> expenses) {
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Przychody");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Wydatki");

        for (GroupStatEntry entry : incomes) {
            incomeSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        for (GroupStatEntry entry : expenses) {
            expenseSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        incomeExpenseLineChart.getData().setAll(incomeSeries, expenseSeries);
    }

    private void populateCategoryExpenseBarChart(List<GroupStatEntry> expenseByCategory) {
        XYChart.Series<String, Number> categorySeries = new XYChart.Series<>();
        categorySeries.setName("Wydatki na Kategorię");

        for (GroupStatEntry entry : expenseByCategory) {
            categorySeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        categoryExpenseBarChart.getData().setAll(categorySeries);
    }
}
