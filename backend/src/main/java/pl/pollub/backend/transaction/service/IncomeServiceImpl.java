package pl.pollub.backend.transaction.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.transaction.amount.AmountExpressionInterpreter;
import pl.pollub.backend.transaction.factory.IncomeFactory;
import pl.pollub.backend.transaction.factory.TransactionFactory;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.repository.IncomeRepository;
import pl.pollub.backend.transaction.repository.TransactionRepository;
import pl.pollub.backend.transaction.service.interfaces.IncomeService;

/**
 * Service for managing incomes. It provides methods for adding, updating and deleting incomes.
 */
@Service
@RequiredArgsConstructor
@Getter
public class IncomeServiceImpl implements IncomeService {
    private final IncomeRepository incomeRepository;
    private final GroupService groupService;
    private final CategoryService categoryService;
    private final AmountExpressionInterpreter amountExpressionInterpreter;
    private final IncomeFactory incomeFactory;

    @Override
    public TransactionRepository<Income> getTransactionRepository() {
        return incomeRepository;
    }

    @Override
    public TransactionFactory<Income> getTransactionFactory() {
        return incomeFactory;
    }
}