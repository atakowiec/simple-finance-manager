package pl.pollub.backend.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;

@Data
public class UserLimitDto {

    @DecimalMin(value = "0.0", message = "Limit musi być większy lub równy 0.")
    private Double spendingLimit;
}
