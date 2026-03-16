package pl.pollub.backend.auth.user.deletion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.deletion.handlers.*;
import pl.pollub.backend.exception.HttpException;

// start mediator
/**
 * Concrete mediator implementation for user deletion workflow.
 * Coordinates multiple handlers to execute the deletion in the correct order.
 * This implements the Mediator design pattern by:
 * - Decoupling handlers from each other
 * - Centralizing workflow orchestration
 * - Managing handler execution order
 * - Providing shared context between handlers
 */
@Service
@RequiredArgsConstructor
public class UserDeletionMediatorImpl implements UserDeletionMediator {
    private static final String ANONYMIZED_USERNAME = "deleted-user";

    // Handlers injected in the order they should execute
    private final GroupOwnershipTransferHandler groupOwnershipTransferHandler;
    private final AnonymizedUserProviderHandler anonymizedUserProviderHandler;
    private final TransactionAnonymizationHandler transactionAnonymizationHandler;
    private final GroupInviteCleanupHandler groupInviteCleanupHandler;
    private final UserEntityRemovalHandler userEntityRemovalHandler;

    @Override
    @Transactional
    public String deleteUser(User user) {
        validateDeletion(user);

        UserDeletionContext context = new UserDeletionContext();

        // Execute workflow as a composite tree of tasks
        CompositeDeletionTask groupCleanup = new CompositeDeletionTask("Group cleanup")
                .add(groupOwnershipTransferHandler)
                .add(groupInviteCleanupHandler);

        CompositeDeletionTask anonymization = new CompositeDeletionTask("Anonymization")
                .add(anonymizedUserProviderHandler)
                .add(transactionAnonymizationHandler);

        CompositeDeletionTask root = new CompositeDeletionTask("User deletion workflow")
                .add(groupCleanup)
                .add(anonymization)
                .add(userEntityRemovalHandler);

        root.handle(user, context);

        return buildResultMessage(context);
    }

    private void validateDeletion(User user) {
        if (ANONYMIZED_USERNAME.equals(user.getUsername())) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Nie można usunąć użytkownika systemowego do anonimizacji.");
        }
    }

    private String buildResultMessage(UserDeletionContext context) {
        return String.format(
                "Użytkownik został pomyślnie usunięty. Własność %d grup przeniesiona, %d transakcji zanonimizowanych.",
                context.getGroupsTransferred(),
                context.getTransactionsAnonymized()
        );
    }
}

