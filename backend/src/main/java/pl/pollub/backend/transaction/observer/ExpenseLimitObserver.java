package pl.pollub.backend.transaction.observer;

// start interface segregation
// start observer
/**
 * Observer for reactions to expense-limit events.
 */
@FunctionalInterface
public interface ExpenseLimitObserver {
    void update(ExpenseLimitEvent event);
}

