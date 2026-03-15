package pl.pollub.backend.group.observer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Spring-managed Subject implementation for group membership change events.
 */
@Service
public class GroupMemberChangeNotificationDispatcher implements GroupMemberChangeSubject {
    private final List<GroupMemberChangeObserver> observers = new CopyOnWriteArrayList<>();

    public GroupMemberChangeNotificationDispatcher(List<GroupMemberChangeObserver> initialObservers) {
        observers.addAll(initialObservers);
    }

    @Override
    public void registerObserver(GroupMemberChangeObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(GroupMemberChangeObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(GroupMemberChangeEvent event) {
        for (GroupMemberChangeObserver observer : observers) {
            observer.update(event);
        }
    }
}

