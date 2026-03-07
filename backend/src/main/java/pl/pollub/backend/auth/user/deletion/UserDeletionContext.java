package pl.pollub.backend.auth.user.deletion;

import lombok.Data;
import pl.pollub.backend.auth.user.User;

/**
 * Context object for sharing state between deletion handlers.
 * Implements the Mediator pattern's context component.
 */
@Data
public class UserDeletionContext {
    private User anonymizedUser;
    private int groupsTransferred;
    private int transactionsAnonymized;
    private int invitesDeleted;
}

