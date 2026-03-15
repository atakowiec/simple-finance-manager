package pl.pollub.backend.group;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.dto.TransactionDto;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.model.Transaction;
import pl.pollub.backend.transaction.observer.ExpenseLimitEvent;
import pl.pollub.backend.transaction.observer.ExpenseLimitSubject;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.repository.IncomeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Facade for importing transactions into a group.
 */
@Service
@RequiredArgsConstructor
public class GroupImportFacade {
    private final CategoryService categoryService;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseLimitSubject expenseLimitSubject;

    private record ImportContext(User user, Group group, Map<Long, TransactionCategory> categories) {}

    public void importTransactions(User user, Group group, ImportExportDto importExportDto) {
        ImportContext importContext = new ImportContext(user, group, loadCategories());

        boolean hasCurrentMonthImportedExpense = importExpensesFromDto(importContext, importExportDto.getExpenses());
        importIncomesFromDto(importContext, importExportDto.getIncomes());

        notifyIfNeeded(user, group, hasCurrentMonthImportedExpense);
    }

    private Map<Long, TransactionCategory> loadCategories() {
        return categoryService.getAllCategories().stream()
                .collect(Collectors.toMap(TransactionCategory::getId, v -> v));
    }

    private boolean importExpensesFromDto(ImportContext importContext, List<TransactionDto> expenses) {
        LocalDate startOfTheMonth = LocalDate.now().withDayOfMonth(1);
        boolean hasCurrentMonthImportedExpense = false;

        for (TransactionDto expense : expenses) {
            TransactionCategory category = validateCategory(expense.getCategory().getId(), importContext.categories());
            Expense newExpense = createExpenseEntity(importContext, expense, category);

            if (expense.getDate() != null && !expense.getDate().isBefore(startOfTheMonth)) {
                hasCurrentMonthImportedExpense = true;
            }

            expenseRepository.save(newExpense);
        }

        return hasCurrentMonthImportedExpense;
    }

    private void importIncomesFromDto(ImportContext importContext, List<TransactionDto> incomes) {
        for (TransactionDto income : incomes) {
            TransactionCategory category = validateCategory(income.getCategory().getId(), importContext.categories());
            Income newIncome = createIncomeEntity(importContext, income, category);
            incomeRepository.save(newIncome);
        }
    }

    private TransactionCategory validateCategory(Long categoryId, Map<Long, TransactionCategory> categories) {
        TransactionCategory category = categories.get(categoryId);
        if (category == null) {
            throw new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + categoryId);
        }
        return category;
    }

    private Expense createExpenseEntity(ImportContext importContext, TransactionDto expense, TransactionCategory category) {
        Expense newExpense = new Expense();
        assignTransactionData(importContext, expense, category, newExpense);
        return newExpense;
    }

    private Income createIncomeEntity(ImportContext importContext, TransactionDto income, TransactionCategory category) {
        Income newIncome = new Income();
        assignTransactionData(importContext, income, category, newIncome);
        return newIncome;
    }

    private <T extends Transaction> void assignTransactionData(ImportContext importContext, TransactionDto transaction, TransactionCategory category, T transactionBase) {
        transactionBase.setName(transaction.getName());
        transactionBase.setAmount(transaction.getAmount());
        transactionBase.setCategory(category);
        transactionBase.setDate(transaction.getDate());
        transactionBase.setGroup(importContext.group());
        transactionBase.setUser(importContext.user());
    }

    private void notifyIfNeeded(User user, Group group, boolean hasCurrentMonthImportedExpense) {
        if (hasCurrentMonthImportedExpense) {
            expenseLimitSubject.notifyObservers(new ExpenseLimitEvent(user, group));
        }
    }
}
