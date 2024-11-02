package pl.pollub.frontend.model.transaction;

import lombok.Data;

import java.time.LocalDate;


@Data
public abstract class Transaction {
    private int id;
    private String name;
    private double amount;
    private int userId;
    private String username;
    private TransactionCategory category;
    private int groupId;
    private String date;

    public abstract String getAmountWithSign();

    protected String getAmountFormatted() {
        return String.format("%.2f", amount);
    }

    public LocalDate getDate() {
        return LocalDate.parse(date);
    }

    public abstract String getAmountStyleClass();
}
