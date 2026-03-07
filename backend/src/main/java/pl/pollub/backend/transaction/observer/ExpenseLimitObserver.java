package pl.pollub.backend.transaction.observer;

// start interface segregation
/**
 * Observer for reactions to expense-limit events.
 */
public interface ExpenseLimitObserver {
    void update(ExpenseLimitEvent event);
}

