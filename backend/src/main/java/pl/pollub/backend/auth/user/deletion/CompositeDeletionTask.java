package pl.pollub.backend.auth.user.deletion;

import pl.pollub.backend.auth.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Composite node for deletion workflow.
 * Executes child tasks in order.
 */
public class CompositeDeletionTask implements UserDeletionTask {
    private final String name;
    private final List<UserDeletionTask> children = new ArrayList<>();

    public CompositeDeletionTask(String name) {
        this.name = name;
    }

    public CompositeDeletionTask add(UserDeletionTask task) {
        Objects.requireNonNull(task, "task");
        children.add(task);
        return this;
    }

    public CompositeDeletionTask addAll(List<? extends UserDeletionTask> tasks) {
        for (UserDeletionTask task : tasks) {
            add(task);
        }
        return this;
    }

    public List<UserDeletionTask> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public String getName() {
        return name;
    }

    @Override
    public void handle(User user, UserDeletionContext context) {
        for (UserDeletionTask task : children) {
            task.handle(user, context);
        }
    }
}
