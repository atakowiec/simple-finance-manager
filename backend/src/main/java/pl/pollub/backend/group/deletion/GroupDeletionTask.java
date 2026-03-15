package pl.pollub.backend.group.deletion;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

/**
 * Base contract for group deletion workflow steps.
 */
public interface GroupDeletionTask {
    void handle(User user, Group group, GroupDeletionContext context);
}

