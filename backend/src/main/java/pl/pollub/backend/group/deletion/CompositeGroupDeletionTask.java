package pl.pollub.backend.group.deletion;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// start composite
/**
 * Composite node that executes child deletion tasks in order.
 */
public class CompositeGroupDeletionTask implements GroupDeletionTask {
    private final List<GroupDeletionTask> children = new ArrayList<>();

    public CompositeGroupDeletionTask add(GroupDeletionTask task) {
        Objects.requireNonNull(task, "task");
        children.add(task);
        return this;
    }

    public List<GroupDeletionTask> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void handle(User user, Group group, GroupDeletionContext context) {
        for (GroupDeletionTask task : children) {
            task.handle(user, group, context);
        }
    }
}

