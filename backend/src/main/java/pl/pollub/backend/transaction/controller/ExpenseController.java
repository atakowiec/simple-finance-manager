package pl.pollub.backend.transaction.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.service.interfaces.ExpenseService;
import pl.pollub.backend.transaction.service.interfaces.TransactionService;

/**
 * Controller for managing expenses.
 */
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Tag(name = "Wydatki", description = "Zarządzanie wydatkami")
public class ExpenseController extends TransactionController<Expense> {
    private final ExpenseService expenseService;

    @Override
    public TransactionService<Expense> getTransactionService() {
        return expenseService;
    }
}
