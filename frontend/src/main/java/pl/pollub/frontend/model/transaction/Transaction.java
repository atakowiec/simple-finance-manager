package pl.pollub.frontend.model.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    public abstract String getAmountWithSign();

    @JsonIgnore
    protected String getAmountFormatted() {
        return String.format("%.2f", amount);
    }

    @JsonIgnore
    public LocalDate getLocalDate() {
        return LocalDate.parse(date);
    }

    @JsonIgnore
    public abstract String getAmountStyleClass();
}
