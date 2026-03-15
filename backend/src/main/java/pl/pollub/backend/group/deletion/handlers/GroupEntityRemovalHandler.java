package pl.pollub.backend.group.deletion.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.deletion.GroupDeletionContext;
import pl.pollub.backend.group.deletion.GroupDeletionHandler;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.repository.GroupRepository;

/**
 * Removes the group entity as the final deletion step.
 */
@Component
@RequiredArgsConstructor
public class GroupEntityRemovalHandler implements GroupDeletionHandler {
    private final GroupRepository groupRepository;

    @Override
    public void handle(User user, Group group, GroupDeletionContext context) {
        groupRepository.delete(group);
        context.setCleanupStepsExecuted(context.getCleanupStepsExecuted() + 1);
    }
}

