package pl.pollub.backend.transaction.observer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Spring-managed Subject implementation for expense-limit events.
 */
@Service
public class ExpenseLimitNotificationDispatcher implements ExpenseLimitSubject {
    private final List<ExpenseLimitObserver> observers = new CopyOnWriteArrayList<>();

    public ExpenseLimitNotificationDispatcher(List<ExpenseLimitObserver> initialObservers) {
        observers.addAll(initialObservers);
    }

    @Override
    public void registerObserver(ExpenseLimitObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ExpenseLimitObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(ExpenseLimitEvent event) {
        for (ExpenseLimitObserver observer : observers) {
            observer.update(event);
        }
    }
}

