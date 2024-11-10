package pl.pollub.backend.expenses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryRepository;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.expenses.dto.ExpenseCreateDto;
import pl.pollub.backend.expenses.dto.ExpenseUpdateDto;
import pl.pollub.backend.group.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.notifications.MailService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Tag(name = "Wydatki", description = "Zarządzanie wydatkami")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final CategoryRepository categoryRepository;
    private final GroupService groupService;
    private final MailService mailService;

    @Operation(summary = "Pobierz wszystkie wydatki dla grupy")
    @ApiResponse(responseCode = "200", description = "Lista wydatków")
    @GetMapping("/{groupId}")
    public List<Expense> getExpenses(@PathVariable Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return expenseService.getAllExpensesForGroup(user, groupId);
    }

    @Operation(summary = "Stwórz nowy wydatek")
    @ApiResponse(responseCode = "201", description = "Stworzono wydatek")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense createExpense(@Valid @RequestBody ExpenseCreateDto expenseCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        TransactionCategory category = categoryRepository.findById(expenseCreateDto.getCategoryId())
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii"));

        Group group = groupService.getGroupByIdOrThrow(expenseCreateDto.getGroupId());
        groupService.checkMembershipOrThrow(user, group);

        Expense expense = new Expense();
        expense.setName(expenseCreateDto.getName());
        expense.setAmount(expenseCreateDto.getAmount());
        expense.setCategory(category);
        expense.setUser(user);
        expense.setDate(expenseCreateDto.getDate());
        expense.setGroup(group);

        Expense newExpense = expenseService.addExpense(expense);

        // we want to send a warning mail only if the expense was created for the current month
        if (expenseCreateDto.getDate().isAfter(LocalDate.now().withDayOfMonth(1)))
            mailService.trySendLimitWarningMail(user, group);

        return newExpense;
    }


    @Operation(summary = "Aktualizuj wydatek")
    @ApiResponse(responseCode = "200", description = "Zaktualizowano wydatek")
    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseUpdateDto expenseUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return expenseService.updateExpense(id, expenseUpdateDto, user);
    }

    @Operation(summary = "Usuń wydatek")
    @ApiResponse(responseCode = "204", description = "Usunięto wydatek")
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        expenseService.deleteExpense(id, user);
    }

    @Operation(summary = "Pobierz statystyki wydatków dla grupy podzielone na dni")
    @ApiResponse(responseCode = "200", description = "Statystyki wydatków")
    @GetMapping("/{groupId}/stats/by-day")
    public Map<String, Double> getThisMonthStatsByDay(@AuthenticationPrincipal User user, @PathVariable Long groupId) {
        return expenseService.getThisMonthStatsByDay(user, groupId);
    }

    @Operation(summary = "Pobierz statystyki wydatków dla grupy podzielone na kategorie")
    @ApiResponse(responseCode = "200", description = "Statystyki wydatków ")
    @GetMapping("/{groupId}/stats/categories")
    public Map<String, Double> getThisMonthCategoryStats(@AuthenticationPrincipal User user, @PathVariable Long groupId) {
        return expenseService.getThisMonthCategoryStats(user, groupId);
    }
}
