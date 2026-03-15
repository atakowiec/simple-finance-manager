package pl.pollub.backend.group.observer;

/**
 * Subject responsible for dispatching group member change events to observers.
 */
public interface GroupMemberChangeSubject {
    void registerObserver(GroupMemberChangeObserver observer);

    void removeObserver(GroupMemberChangeObserver observer);

    void notifyObservers(GroupMemberChangeEvent event);
}

