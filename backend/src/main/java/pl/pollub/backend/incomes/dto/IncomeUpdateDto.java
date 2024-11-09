package pl.pollub.backend.incomes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class IncomeUpdateDto {
    @NotBlank
    private String name;
    @NotNull
    private Double amount;
    @NotNull
    private Long categoryId;

    private LocalDate date;
}