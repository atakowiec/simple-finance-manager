package pl.pollub.backend.expenses;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryRepository;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.expenses.dto.ExpenseUpdateDto;
import pl.pollub.backend.group.GroupService;
import pl.pollub.backend.group.model.Group;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing expenses. It provides methods for adding, updating and deleting expenses.
 */
@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final GroupService groupService;

    /**
     * Returns all expenses for the specified user in the specified group.
     *
     * @param user    user whose expenses will be returned
     * @param groupId group for which the expenses will be returned
     * @return list of expenses
     */
    public List<Expense> getAllExpensesForGroup(User user, long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        return expenseRepository.findAllByGroup(group);
    }

    /**
     * Saves the specified expense to the database.
     *
     * @param expense expense to be saved
     * @return saved expense
     */
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    /**
     * Updates the expense with the specified id. The expense must belong to the specified user.
     *
     * @param id             id of the expense to be updated
     * @param updatedExpense updated expense data transfer object
     * @param user           user who owns the expense
     * @return updated expense
     */
    public Expense updateExpense(Long id, ExpenseUpdateDto updatedExpense, User user) {
        return expenseRepository.findByIdAndUser(id, user)
                .map(expense -> {
                    if (updatedExpense.getName() != null) {
                        expense.setName(updatedExpense.getName());
                    }
                    if (updatedExpense.getAmount() != null) {
                        expense.setAmount(updatedExpense.getAmount());
                    }
                    if (updatedExpense.getCategoryId() != null) {
                        TransactionCategory category = categoryRepository.findById(updatedExpense.getCategoryId())
                                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + updatedExpense.getCategoryId()));
                        expense.setCategory(category);
                    }
                    if (updatedExpense.getDate() != null) {
                        expense.setDate(updatedExpense.getDate());
                    }
                    return expenseRepository.save(expense);
                })
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono wydatku o podanym identyfikatorze: " + id));
    }

    /**
     * Deletes the expense with the specified id. The expense must belong to the specified user.
     *
     * @param id   id of the expense to be deleted
     * @param user user who owns the expense
     */
    public void deleteExpense(Long id, User user) {
        if (expenseRepository.existsByIdAndUser(id, user)) {
            expenseRepository.deleteById(id);
        } else {
            throw new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono wydatku o podanym identyfikatorze: " + id);
        }
    }

    /**
     * Returns the total amount of expenses for the specified group in the current month.
     *
     * @param user    user who wants to get the statistics
     * @param groupId group for which the statistics will be returned
     * @return total amount of expenses for the current month
     */
    public Map<String, Double> getThisMonthCategoryStats(User user, Long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        LocalDate now = LocalDate.now();
        List<Object[]> result = expenseRepository.sumAllByGroupAndMinDate(group, LocalDate.of(now.getYear(), now.getMonthValue(), 1));
        Map<String, Double> stats = new HashMap<>();

        for (Object[] row : result) {
            stats.compute(row[0].toString(), (k, v) -> v == null ? (Double) row[1] : v + (Double) row[1]);
        }

        return stats;
    }

    /**
     * Returns the total amount of expenses for the specified group in the current month. The expenses are grouped by day.
     *
     * @param user    user who wants to get the statistics
     * @param groupId group for which the statistics will be returned
     * @return total amount of expenses for the current month
     */
    public Map<String, Double> getThisMonthStatsByDay(User user, Long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        LocalDate now = LocalDate.now();
        List<Object[]> result = expenseRepository.findAllByGroupAndMinDateAndGroupByDate(group, LocalDate.of(now.getYear(), now.getMonthValue(), 1));

        Map<String, Double> stats = new HashMap<>();

        for (Object[] row : result) {
            stats.compute(row[0].toString(), (k, v) -> v == null ? (Double) row[1] : v + (Double) row[1]);
        }

        return stats;
    }
}
