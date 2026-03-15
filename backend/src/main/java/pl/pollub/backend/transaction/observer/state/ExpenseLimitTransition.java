package pl.pollub.backend.transaction.observer.state;

/**
 * Result of one budget lifecycle evaluation.
 */
public record ExpenseLimitTransition(
        ExpenseLimitLifecycleStatus previousState,
        ExpenseLimitLifecycleStatus currentState,
        boolean transitioned
) {
}

