package pl.pollub.backend.expenses.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for updating expense.
 */
@Data
public class ExpenseUpdateDto {
    @NotBlank
    private String name;
    @NotNull
    private Double amount;
    @NotNull
    private Long categoryId;

    private LocalDate date;
}
