package pl.pollub.backend.transaction.observer.state;

/**
 * Persisted lifecycle status of a group's monthly expense limit.
 */
public enum ExpenseLimitLifecycleStatus {
    NO_LIMIT,
    WITHIN_LIMIT,
    NEAR_LIMIT,
    EXCEEDED
}

