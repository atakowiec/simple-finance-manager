package pl.pollub.backend.transaction.observer.state;

/**
 * Shared transition logic for concrete expense-limit states.
 */
abstract class AbstractExpenseLimitState implements ExpenseLimitState {
    @Override
    public ExpenseLimitState determineNextState(ExpenseLimitMetrics metrics) {
        if (!metrics.hasLimit()) {
            return NoLimitState.INSTANCE;
        }
        if (metrics.isExceeded()) {
            return ExceededLimitState.INSTANCE;
        }
        if (metrics.isNearLimit()) {
            return NearLimitState.INSTANCE;
        }
        return WithinLimitState.INSTANCE;
    }
}

