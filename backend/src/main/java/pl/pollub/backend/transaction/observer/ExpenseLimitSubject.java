package pl.pollub.backend.transaction.observer;

// start dependency inversion principle
// start interface segregation principle
/**
 * Subject responsible for dispatching expense-limit events to observers.
 */
public interface ExpenseLimitSubject {
    void registerObserver(ExpenseLimitObserver observer);

    void removeObserver(ExpenseLimitObserver observer);

    void notifyObservers(ExpenseLimitEvent event);
}

