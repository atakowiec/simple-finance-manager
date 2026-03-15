package pl.pollub.backend.group.deletion;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

/**
 * Handler contract for single-responsibility group deletion steps.
 */
public interface GroupDeletionHandler extends GroupDeletionTask {
    @Override
    void handle(User user, Group group, GroupDeletionContext context);
}

