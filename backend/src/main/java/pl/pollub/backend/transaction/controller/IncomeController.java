package pl.pollub.backend.transaction.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.service.interfaces.IncomeService;
import pl.pollub.backend.transaction.service.interfaces.TransactionService;

/**
 * Controller for managing expenses.
 */
@RestController
@RequestMapping("/incomes")
@RequiredArgsConstructor
@Tag(name = "Wydatki", description = "Zarządzanie wydatkami")
public class IncomeController extends TransactionController<Income> {
    private final IncomeService incomeService;

    @Override
    public TransactionService<Income> getTransactionService() {
        return incomeService;
    }
}
