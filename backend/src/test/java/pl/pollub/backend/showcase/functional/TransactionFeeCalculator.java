package pl.pollub.backend.showcase.functional;

@FunctionalInterface
public interface TransactionFeeCalculator {
    double calculateFee(double amount, double feePercent);
}

