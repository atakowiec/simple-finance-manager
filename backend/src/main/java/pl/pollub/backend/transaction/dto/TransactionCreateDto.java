package pl.pollub.backend.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for creating new income. It holds information about income name, amount, category, group and date.
 */
@Data
public class TransactionCreateDto {
    @NotBlank(message = "Nazwa jest wymagana")
    private String name;

    @NotBlank(message = "Kwota jest wymagana")
    private String amount;

    @NotNull(message = "Kategoria jest wymagana")
    private Long categoryId;

    @NotNull(message = "Grupa jest wymagana")
    private Long groupId;

    private LocalDate date = LocalDate.now();
}

