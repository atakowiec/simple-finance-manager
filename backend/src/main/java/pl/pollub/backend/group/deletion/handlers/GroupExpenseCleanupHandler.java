package pl.pollub.backend.group.deletion.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.deletion.GroupDeletionContext;
import pl.pollub.backend.group.deletion.GroupDeletionHandler;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.repository.ExpenseRepository;

/**
 * Removes all expenses assigned to the deleted group.
 */
@Component
@RequiredArgsConstructor
public class GroupExpenseCleanupHandler implements GroupDeletionHandler {
    private final ExpenseRepository expenseRepository;

    @Override
    public void handle(User user, Group group, GroupDeletionContext context) {
        expenseRepository.deleteAllByGroup(group);
        context.setCleanupStepsExecuted(context.getCleanupStepsExecuted() + 1);
    }
}

