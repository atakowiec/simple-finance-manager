package pl.pollub.backend.transaction.observer;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

/**
 * Event payload published when expense-limit notifications should be evaluated.
 */
public record ExpenseLimitEvent(User user, Group group) {
}

