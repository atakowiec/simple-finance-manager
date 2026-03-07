package pl.pollub.backend.showcase.functional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FunctionalInterfaceShowcaseTest {

    @Test
    void functionalInterfacesCanBeUsedWithLambdas() {
        SpendingLimitEvaluator limitEvaluator = (spentAmount, monthlyLimit) -> spentAmount > monthlyLimit;
        TransactionFeeCalculator feeCalculator = (amount, feePercent) -> amount * (feePercent / 100.0);
        SavingsGoalProgressFormatter progressFormatter = (savedAmount, targetAmount) -> {
            double progressPercent = (savedAmount / targetAmount) * 100.0;
            return String.format("%.1f%% goal reached", progressPercent);
        };

        double spentAmount = currentSpentAmount();
        double monthlyLimit = currentMonthlyLimit();

        boolean exceeded = limitEvaluator.isLimitExceeded(spentAmount, monthlyLimit);
        double fee = feeCalculator.calculateFee(200.0, 1.5);
        String progress = progressFormatter.formatProgress(450.0, 1000.0);

        Assertions.assertTrue(exceeded);
        Assertions.assertEquals(3.0, fee, 0.0001);
        Assertions.assertEquals("45.0% goal reached", progress);
    }

    private double currentSpentAmount() {
        return 1250.0;
    }

    private double currentMonthlyLimit() {
        return 1000.0;
    }
}
