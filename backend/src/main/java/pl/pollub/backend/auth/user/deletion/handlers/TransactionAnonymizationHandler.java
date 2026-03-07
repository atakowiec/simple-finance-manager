package pl.pollub.backend.auth.user.deletion.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.deletion.UserDeletionContext;
import pl.pollub.backend.auth.user.deletion.UserDeletionHandler;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.repository.IncomeRepository;

/**
 * Handler responsible for anonymizing user's transactions.
 */
@Component
@RequiredArgsConstructor
public class TransactionAnonymizationHandler implements UserDeletionHandler {
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    @Override
    public void handle(User user, UserDeletionContext context) {
        User anonymizedUser = context.getAnonymizedUser();
        if (anonymizedUser == null) {
            throw new IllegalStateException("Anonymized user must be provided before transaction anonymization");
        }

        int expensesAnonymized = expenseRepository.anonymizeUserTransactions(user, anonymizedUser);
        int incomesAnonymized = incomeRepository.anonymizeUserTransactions(user, anonymizedUser);

        context.setTransactionsAnonymized(expensesAnonymized + incomesAnonymized);
    }
}

