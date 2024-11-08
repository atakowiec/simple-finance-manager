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

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final GroupService groupService;

    public List<Expense> getAllExpensesForGroup(User user, long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        return expenseRepository.findAllByGroup(group);
    }

    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

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

    public void deleteExpense(Long id, User user) {
        if (expenseRepository.existsByIdAndUser(id, user)) {
            expenseRepository.deleteById(id);
        } else {
            throw new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono wydatku o podanym identyfikatorze: " + id);
        }
    }

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
