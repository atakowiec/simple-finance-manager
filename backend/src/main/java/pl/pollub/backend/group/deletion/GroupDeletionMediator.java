package pl.pollub.backend.group.deletion;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

// start mediator
/**
 * Mediator interface for coordinating group deletion workflow.
 */
public interface GroupDeletionMediator {
    void deleteGroup(User user, Group group);
}

