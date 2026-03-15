package pl.pollub.backend.transaction.observer.state;

/**
 * State used when a group is close to its monthly expense limit.
 */
public final class NearLimitState extends AbstractExpenseLimitState {
    public static final NearLimitState INSTANCE = new NearLimitState();

    private NearLimitState() {
    }

    @Override
    public ExpenseLimitLifecycleStatus getStatus() {
        return ExpenseLimitLifecycleStatus.NEAR_LIMIT;
    }
}

