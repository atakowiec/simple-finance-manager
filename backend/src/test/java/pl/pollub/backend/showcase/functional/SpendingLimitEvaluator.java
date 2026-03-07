package pl.pollub.backend.showcase.functional;

@FunctionalInterface
public interface SpendingLimitEvaluator {
    boolean isLimitExceeded(double spentAmount, double monthlyLimit);
}

