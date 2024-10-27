package pl.pollub.frontend.model.transaction;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public abstract class Transaction {
    private int id;
    private String name;
    private double amount;
    private TransactionCategory category;
    private String date;

    public abstract String getAmountWithSign();

    protected String getAmountFormatted() {
        return String.format("%.2f", amount);
    }

    public LocalDateTime getDate() {
        return LocalDateTime.parse(date);
    }

    public abstract String getAmountStyleClass();
}