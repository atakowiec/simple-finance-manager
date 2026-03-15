package pl.pollub.backend.transaction.observer.state;

/**
 * State used when a group is safely within its monthly expense limit.
 */
public final class WithinLimitState extends AbstractExpenseLimitState {
    public static final WithinLimitState INSTANCE = new WithinLimitState();

    private WithinLimitState() {
    }

    @Override
    public ExpenseLimitLifecycleStatus getStatus() {
        return ExpenseLimitLifecycleStatus.WITHIN_LIMIT;
    }
}

