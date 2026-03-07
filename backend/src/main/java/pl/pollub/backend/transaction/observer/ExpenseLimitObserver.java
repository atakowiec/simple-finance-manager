package pl.pollub.backend.transaction.observer;

/**
 * Observer for reactions to expense-limit events.
 */
public interface ExpenseLimitObserver {
    void update(ExpenseLimitEvent event);
}

