package pl.pollub.backend.transaction.factory;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.group.model.Group;

/**
 * Context object used when creating transactions.
 */
public record TransactionFactoryContext(
        User user,
        TransactionCategory category,
        Group group
) {
}

