package pl.pollub.frontend.controller.group.add;

import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.transaction.TransactionCategory;
import pl.pollub.frontend.service.CategoryService;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public class AddIncomeController extends AbstractAddTransactionController {
    @Inject
    private CategoryService categoryService;

    @Override
    protected HttpResponse<String> addTransaction(String name, double amount, TransactionCategory category, LocalDate date) {
        return super.transactionService.addIncome(name, amount, category, date, getGroup().getId());
    }

    @Override
    protected List<TransactionCategory> getCategories() {
        return categoryService.getIncomeCategories();
    }
}
