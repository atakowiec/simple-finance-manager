package pl.pollub.backend.expenses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.categories.CategoryRepository;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.expenses.dto.ExpenseCreateDto;
import pl.pollub.backend.expenses.dto.ExpenseUpdateDto;
import pl.pollub.backend.group.GroupRepository;
import pl.pollub.backend.group.GroupService;
import pl.pollub.backend.group.model.Group;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final GroupService groupService;

    @GetMapping("/{groupId}")
    public List<Expense> getExpenses(@PathVariable Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return expenseService.getAllExpensesForGroup(user, groupId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense createExpense(@Valid @RequestBody ExpenseCreateDto expenseCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        TransactionCategory category = categoryRepository.findById(expenseCreateDto.getCategoryId())
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + expenseCreateDto.getCategoryId()));

        Group group = groupService.getGroupByIdOrThrow(expenseCreateDto.getGroupId());
        groupService.checkMembershipOrThrow(user, group);

        Expense expense = new Expense();
        expense.setName(expenseCreateDto.getName());
        expense.setAmount(expenseCreateDto.getAmount());
        expense.setCategory(category);
        expense.setUser(user);
        expense.setDate(expenseCreateDto.getDate());
        expense.setGroup(group);

        return expenseService.addExpense(expense);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseUpdateDto expenseUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return expenseService.updateExpense(id, expenseUpdateDto, user);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        expenseService.deleteExpense(id, user);
    }

    @GetMapping("/{groupId}/stats/by-day")
    public Map<String, Double> getThisMonthStatsByDay(@AuthenticationPrincipal User user, @PathVariable Long groupId) {
        return expenseService.getThisMonthStatsByDay(user, groupId);
    }

    @GetMapping("/{groupId}/stats/categories")
    public Map<String, Double> getThisMonthCategoryStats(@AuthenticationPrincipal User user, @PathVariable Long groupId) {
        return expenseService.getThisMonthCategoryStats(user, groupId);
    }
}
