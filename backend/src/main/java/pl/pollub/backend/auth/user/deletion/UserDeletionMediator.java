package pl.pollub.backend.auth.user.deletion;

import pl.pollub.backend.auth.user.User;

// start mediator
/**
 * Mediator interface for coordinating user deletion workflow.
 * Implements the Mediator design pattern to decouple user deletion orchestration
 * from individual services and handlers.
 */
public interface UserDeletionMediator {
    /**
     * Orchestrates the complete user deletion workflow including:
     * - Group ownership transfer
     * - Transaction anonymization
     * - Invite cleanup
     * - User removal
     *
     * @param user the user to be deleted
     * @return result message describing the deletion outcome
     */
    String deleteUser(User user);
}

