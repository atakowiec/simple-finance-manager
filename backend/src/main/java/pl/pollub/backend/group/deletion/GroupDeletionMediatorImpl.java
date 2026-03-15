package pl.pollub.backend.group.deletion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.deletion.handlers.GroupDeletionActivityLogHandler;
import pl.pollub.backend.group.deletion.handlers.GroupEntityRemovalHandler;
import pl.pollub.backend.group.deletion.handlers.GroupExpenseCleanupHandler;
import pl.pollub.backend.group.deletion.handlers.GroupHistoryCleanupHandler;
import pl.pollub.backend.group.deletion.handlers.GroupIncomeCleanupHandler;
import pl.pollub.backend.group.deletion.handlers.GroupInviteCleanupHandler;
import pl.pollub.backend.group.model.Group;

/**
 * Concrete mediator that orchestrates group deletion steps.
 */
@Service
@RequiredArgsConstructor
public class GroupDeletionMediatorImpl implements GroupDeletionMediator {
    private final GroupDeletionActivityLogHandler groupDeletionActivityLogHandler;
    private final GroupInviteCleanupHandler groupInviteCleanupHandler;
    private final GroupExpenseCleanupHandler groupExpenseCleanupHandler;
    private final GroupIncomeCleanupHandler groupIncomeCleanupHandler;
    private final GroupHistoryCleanupHandler groupHistoryCleanupHandler;
    private final GroupEntityRemovalHandler groupEntityRemovalHandler;

    @Override
    @Transactional
    public void deleteGroup(User user, Group group) {
        GroupDeletionContext context = new GroupDeletionContext();

        CompositeGroupDeletionTask workflow = new CompositeGroupDeletionTask()
                .add(groupDeletionActivityLogHandler)
                .add(groupInviteCleanupHandler)
                .add(groupExpenseCleanupHandler)
                .add(groupIncomeCleanupHandler)
                .add(groupHistoryCleanupHandler)
                .add(groupEntityRemovalHandler);

        workflow.handle(user, group, context);
    }
}

