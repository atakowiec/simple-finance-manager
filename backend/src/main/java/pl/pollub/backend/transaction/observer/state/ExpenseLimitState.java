package pl.pollub.backend.transaction.observer.state;

// start state
/**
 * State abstraction for the monthly expense-limit lifecycle.
 */
public interface ExpenseLimitState {
    ExpenseLimitLifecycleStatus getStatus();

    ExpenseLimitState determineNextState(ExpenseLimitMetrics metrics);

    static ExpenseLimitState fromStatus(ExpenseLimitLifecycleStatus status) {
        ExpenseLimitLifecycleStatus resolvedStatus = status == null
                ? ExpenseLimitLifecycleStatus.NO_LIMIT
                : status;

        return switch (resolvedStatus) {
            case NO_LIMIT -> NoLimitState.INSTANCE;
            case WITHIN_LIMIT -> WithinLimitState.INSTANCE;
            case NEAR_LIMIT -> NearLimitState.INSTANCE;
            case EXCEEDED -> ExceededLimitState.INSTANCE;
        };
    }
}

