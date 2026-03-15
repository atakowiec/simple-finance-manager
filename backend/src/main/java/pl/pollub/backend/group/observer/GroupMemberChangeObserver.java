package pl.pollub.backend.group.observer;

/**
 * Observer for reactions to group membership changes.
 */
@FunctionalInterface
public interface GroupMemberChangeObserver {
    void update(GroupMemberChangeEvent event);
}

