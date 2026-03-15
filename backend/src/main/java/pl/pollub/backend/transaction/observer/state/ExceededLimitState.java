package pl.pollub.backend.transaction.observer.state;

/**
 * State used when a group's monthly expense limit has been exceeded.
 */
public final class ExceededLimitState extends AbstractExpenseLimitState {
    public static final ExceededLimitState INSTANCE = new ExceededLimitState();

    private ExceededLimitState() {
    }

    @Override
    public ExpenseLimitLifecycleStatus getStatus() {
        return ExpenseLimitLifecycleStatus.EXCEEDED;
    }
}


