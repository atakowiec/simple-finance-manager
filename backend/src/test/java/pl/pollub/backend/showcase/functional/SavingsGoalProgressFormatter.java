package pl.pollub.backend.showcase.functional;

@FunctionalInterface
public interface SavingsGoalProgressFormatter {
    String formatProgress(double savedAmount, double targetAmount);
}

