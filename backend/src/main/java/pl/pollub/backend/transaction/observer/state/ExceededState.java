package pl.pollub.backend.transaction.observer.state;

/**
 * State used when a group's monthly expense limit has been exceeded.
 */
public final class ExceededState extends AbstractExpenseLimitState {
    public static final ExceededState INSTANCE = new ExceededState();

    private ExceededState() {
    }

    @Override
    public ExpenseLimitLifecycleStatus getStatus() {
        return ExpenseLimitLifecycleStatus.EXCEEDED;
    }
}

