package pl.pollub.backend.incomes.dto;

import lombok.Data;

@Data
public class IncomeUpdateDto {
    private String name;
    private Double amount;
    private Long categoryId;
}