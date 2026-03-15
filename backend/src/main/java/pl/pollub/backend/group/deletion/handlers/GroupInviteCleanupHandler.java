package pl.pollub.backend.group.deletion.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.deletion.GroupDeletionContext;
import pl.pollub.backend.group.deletion.GroupDeletionHandler;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.repository.GroupInviteRepository;

/**
 * Removes all invites linked to the deleted group.
 */
@Component("groupDeletionGroupInviteCleanupHandler")
@RequiredArgsConstructor
public class GroupInviteCleanupHandler implements GroupDeletionHandler {
    private final GroupInviteRepository groupInviteRepository;

    @Override
    public void handle(User user, Group group, GroupDeletionContext context) {
        groupInviteRepository.deleteAllByGroup(group);
        context.setCleanupStepsExecuted(context.getCleanupStepsExecuted() + 1);
    }
}

