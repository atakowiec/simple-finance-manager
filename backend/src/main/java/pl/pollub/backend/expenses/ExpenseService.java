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

import java.util.List;

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
}
