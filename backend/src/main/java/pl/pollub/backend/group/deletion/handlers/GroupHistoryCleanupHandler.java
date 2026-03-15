package pl.pollub.backend.group.deletion.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.deletion.GroupDeletionContext;
import pl.pollub.backend.group.deletion.GroupDeletionHandler;
import pl.pollub.backend.group.memento.GroupCaretaker;
import pl.pollub.backend.group.model.Group;

/**
 * Clears undo history snapshots for the deleted group.
 */
@Component
@RequiredArgsConstructor
public class GroupHistoryCleanupHandler implements GroupDeletionHandler {
    private final GroupCaretaker groupCaretaker;

    @Override
    public void handle(User user, Group group, GroupDeletionContext context) {
        groupCaretaker.clearHistory(group.getId());
        context.setCleanupStepsExecuted(context.getCleanupStepsExecuted() + 1);
    }
}

