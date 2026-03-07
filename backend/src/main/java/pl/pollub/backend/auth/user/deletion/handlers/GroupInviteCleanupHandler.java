package pl.pollub.backend.auth.user.deletion.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.deletion.UserDeletionContext;
import pl.pollub.backend.auth.user.deletion.UserDeletionHandler;
import pl.pollub.backend.group.repository.GroupInviteRepository;

/**
 * Handler responsible for cleaning up group invitations.
 */
@Component
@RequiredArgsConstructor
public class GroupInviteCleanupHandler implements UserDeletionHandler {
    private final GroupInviteRepository groupInviteRepository;

    @Override
    public void handle(User user, UserDeletionContext context) {
        groupInviteRepository.deleteAllByInvitee(user);
        groupInviteRepository.deleteAllByInviter(user);
        context.setInvitesDeleted(context.getInvitesDeleted() + 1);
    }
}

