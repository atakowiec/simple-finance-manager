package pl.pollub.backend.expenses.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pollub.backend.categories.dto.CategoryDto;
import pl.pollub.backend.expenses.Expense;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ExpenseDto {
    private Long id;
    private String name;
    private Double amount;
    private CategoryDto category;
    private long userId;
    private String username;
    private long groupId;
    private LocalDate date;

    public ExpenseDto(Expense expense) {
        this.id = expense.getId();
        this.name = expense.getName();
        this.amount = expense.getAmount();
        this.category = expense.getCategory().toDto();
        this.userId = expense.getUser().getId();
        this.username = expense.getUser().getUsername();
        this.groupId = expense.getGroup().getId();
        this.date = expense.getDate();
    }
}
