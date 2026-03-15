package pl.pollub.backend.transaction.observer.state;

/**
 * State used when a group has no active expense limit.
 */
public final class NoLimitState extends AbstractExpenseLimitState {
    public static final NoLimitState INSTANCE = new NoLimitState();

    private NoLimitState() {
    }

    @Override
    public ExpenseLimitLifecycleStatus getStatus() {
        return ExpenseLimitLifecycleStatus.NO_LIMIT;
    }
}

