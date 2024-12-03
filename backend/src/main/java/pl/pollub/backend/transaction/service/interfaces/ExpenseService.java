package pl.pollub.backend.transaction.service.interfaces;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.model.Expense;

public interface ExpenseService extends TransactionService<Expense> {
    void trySendLimitWarningMail(User user, Group group);
}
