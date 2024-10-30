package pl.pollub.backend.auth.dto;

import lombok.Data;

@Data
public class IncomeUpdateDto {
    private String name;
    private Double amount;
    private Long categoryId;
}