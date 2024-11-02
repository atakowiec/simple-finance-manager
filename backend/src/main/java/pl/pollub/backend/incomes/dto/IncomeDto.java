package pl.pollub.backend.incomes.dto;

import lombok.Data;
import pl.pollub.backend.categories.IncomeCategory;
import pl.pollub.backend.incomes.Income;

import java.time.LocalDate;

@Data
public class IncomeDto {
    private Long id;
    private String name;
    private Double amount;
    private IncomeCategory category;
    private long userId;
    private String username;
    private long groupId;
    private LocalDate date;

    public IncomeDto(Income income) {
        this.id = income.getId();
        this.name = income.getName();
        this.amount = income.getAmount();
        this.category = income.getCategory();
        this.userId = income.getUser().getId();
        this.username = income.getUser().getUsername();
        this.groupId = income.getGroup().getId();
        this.date = income.getDate();
    }
}
