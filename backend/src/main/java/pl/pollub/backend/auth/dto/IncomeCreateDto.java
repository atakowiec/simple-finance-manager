package pl.pollub.backend.auth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncomeCreateDto {
    @NotBlank(message = "Nazwa dochodu jest wymagana")
    private String name;

    @NotNull(message = "Kwota dochodu jest wymagana")
    @DecimalMin(value = "0.01", message = "Kwota musi być większa niż 0")
    private Double amount;

    @NotNull(message = "Kategoria dochodu jest wymagana")
    private Long categoryId;

    private LocalDateTime date = LocalDateTime.now();
}

