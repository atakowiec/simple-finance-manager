package pl.pollub.backend.transaction.observer.state;

import pl.pollub.backend.config.constants.ExpenseLimitConstants;

/**
 * Value object with the current monthly budget metrics used by the state machine.
 */
public record ExpenseLimitMetrics(double totalExpenses, double expenseLimit) {
    public boolean hasLimit() {
        return expenseLimit > ExpenseLimitConstants.NO_EXPENSE_LIMIT && expenseLimit > 0;
    }

    public boolean isExceeded() {
        return hasLimit() && totalExpenses > expenseLimit;
    }

    public boolean isNearLimit() {
        if (!hasLimit()) {
            return false;
        }

        double remainingPart = 1.0 - (totalExpenses / expenseLimit);
        return !isExceeded() && remainingPart <= ExpenseLimitConstants.EXPENSE_WARNING_THRESHOLD;
    }
}

