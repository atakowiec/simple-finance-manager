package pl.pollub.backend.transaction.observer.state;

/**
 * State context for the monthly expense-limit lifecycle.
 */
public class BudgetStatusContext {
    private ExpenseLimitState currentState;

    public BudgetStatusContext(ExpenseLimitState currentState) {
        this.currentState = currentState;
    }

    public ExpenseLimitTransition transition(ExpenseLimitMetrics metrics) {
        ExpenseLimitLifecycleStatus previousStatus = currentState.getStatus();
        ExpenseLimitState nextState = currentState.determineNextState(metrics);
        currentState = nextState;

        ExpenseLimitLifecycleStatus currentStatus = currentState.getStatus();
        return new ExpenseLimitTransition(previousStatus, currentStatus, previousStatus != currentStatus);
    }

    public ExpenseLimitState getCurrentState() {
        return currentState;
    }
}

