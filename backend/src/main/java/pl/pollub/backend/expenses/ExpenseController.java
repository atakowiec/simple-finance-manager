package pl.pollub.backend.expenses;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.dto.ExpenseUpdateDto;
import pl.pollub.backend.auth.dto.ExpenseCreateDto;
import pl.pollub.backend.categories.ExpenseCategory;
import pl.pollub.backend.categories.ExpenseCategoryRepository;
import pl.pollub.backend.exception.HttpException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final ExpenseCategoryRepository categoryRepository;

    public ExpenseController(ExpenseService expenseService, ExpenseCategoryRepository categoryRepository) {
        this.expenseService = expenseService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Expense> getExpenses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return expenseService.getAllExpensesForUser(user);
    }

    @PostMapping
    public Expense createExpense(@Valid @RequestBody ExpenseCreateDto expenseCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ExpenseCategory category = categoryRepository.findById(expenseCreateDto.getCategoryId())
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + expenseCreateDto.getCategoryId()));

        Expense expense = new Expense();
        expense.setName(expenseCreateDto.getName());
        expense.setAmount(expenseCreateDto.getAmount());
        expense.setCategory(category);
        expense.setUser(user);
        expense.setDate(expenseCreateDto.getDate());

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
}
