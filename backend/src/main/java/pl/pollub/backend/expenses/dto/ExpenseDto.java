package pl.pollub.backend.expenses.dto;

import lombok.Data;
import pl.pollub.backend.categories.ExpenseCategory;
import pl.pollub.backend.expenses.Expense;

import java.time.LocalDate;

@Data
public class ExpenseDto {
    private Long id;
    private String name;
    private Double amount;
    private ExpenseCategory category;
    private long userId;
    private String username;
    private long groupId;
    private LocalDate date;

    public ExpenseDto(Expense expense) {
        this.id = expense.getId();
        this.name = expense.getName();
        this.amount = expense.getAmount();
        this.category = expense.getCategory();
        this.userId = expense.getUser().getId();
        this.username = expense.getUser().getUsername();
        this.groupId = expense.getGroup().getId();
        this.date = expense.getDate();
    }
}
