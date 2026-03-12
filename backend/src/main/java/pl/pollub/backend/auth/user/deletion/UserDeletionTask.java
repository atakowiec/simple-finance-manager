package pl.pollub.backend.auth.user.deletion;

import pl.pollub.backend.auth.user.User;

/**
 * Component interface for deletion workflow steps.
 * Composite and leaf tasks share the same contract.
 */
public interface UserDeletionTask {
    void handle(User user, UserDeletionContext context);
}
