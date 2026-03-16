package pl.pollub.backend.group.observer;

// start observer
/**
 * Observer for reactions to group membership changes.
 */
@FunctionalInterface
public interface GroupMemberChangeObserver {
    void update(GroupMemberChangeEvent event);
}

