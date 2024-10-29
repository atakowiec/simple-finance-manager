package pl.pollub.backend.auth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpenseCreateDto {
    @NotBlank(message = "Nazwa wydatku jest wymagana")
    private String name;

    @NotNull(message = "Kwota wydatku jest wymagana")
    @DecimalMin(value = "0.01", message = "Kwota musi być większa niż 0")
    private Double amount;

    @NotNull(message = "Kategoria wydatku jest wymagana")
    private Long categoryId;

    private LocalDateTime date = LocalDateTime.now();
}

