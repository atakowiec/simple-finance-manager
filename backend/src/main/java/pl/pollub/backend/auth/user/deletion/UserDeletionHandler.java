package pl.pollub.backend.auth.user.deletion;

import pl.pollub.backend.auth.user.User;

// start interface segregation principle
/**
 * Base interface for user deletion handlers.
 * Each handler is responsible for a specific aspect of user deletion workflow.
 */
public interface UserDeletionHandler extends UserDeletionTask {
    /**
     * Executes the handler's specific deletion task.
     *
     * @param user the user being deleted
     * @param context the deletion context for sharing state between handlers
     */
    @Override
    void handle(User user, UserDeletionContext context);
}

