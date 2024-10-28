package pl.pollub.backend.expenses.dto;

import lombok.Data;

@Data
public class ExpenseUpdateDto {
    private String name;
    private Double amount;
    private Long categoryId;
}
