package pl.pollub.backend.group.deletion.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityMediator;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.deletion.GroupDeletionContext;
import pl.pollub.backend.group.deletion.GroupDeletionHandler;
import pl.pollub.backend.group.model.Group;

/**
 * Writes the group deletion event before entity removal.
 */
@Component
@RequiredArgsConstructor
public class GroupDeletionActivityLogHandler implements GroupDeletionHandler {
    private final ActivityMediator activityMediator;

    @Override
    public void handle(User user, Group group, GroupDeletionContext context) {
        activityMediator.notify(
                ActivityEventType.GROUP_DELETED,
                ActivityEventData.newBuilder()
                        .user(user)
                        .group(group)
                        .resourceName(group.getName())
                        .build()
        );
        context.setCleanupStepsExecuted(context.getCleanupStepsExecuted() + 1);
    }
}

