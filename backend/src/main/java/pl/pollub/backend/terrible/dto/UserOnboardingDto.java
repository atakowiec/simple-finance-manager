package pl.pollub.backend.terrible.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import pl.pollub.backend.config.constants.ValidationConstants;

/**
 * Data transfer object for creating a user, group, and expense in a single operation.
 * Encapsulates all required data for the comprehensive creation process.
 */
@Data
@Builder
public class UserOnboardingDto {

    // User data
    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Length(min = ValidationConstants.USERNAME_MIN_LENGTH, max = ValidationConstants.USERNAME_MAX_LENGTH,
            message = "Nazwa użytkownika musi mieć od 3 do 20 znaków")
    private String username;

    @NotBlank(message = "Hasło jest wymagane")
    @Length(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH,
            message = "Hasło musi mieć od 6 do 30 znaków")
    private String password;

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Niepoprawny format adresu email")
    private String email;

    // Group data
    @NotBlank(message = "Nazwa grupy jest wymagana")
    private String groupName;

    @DecimalMin(value = "0.0", message = "Limit wydatków nie może być ujemny")
    private double expenseLimit;

    // Expense data
    @NotBlank(message = "Nazwa wydatku jest wymagana")
    private String expenseName;

    @NotNull(message = "Kwota jest wymagana")
    @DecimalMin(value = "0.01", message = "Kwota musi być większa niż 0")
    private Double amount;

    @NotNull(message = "Kategoria jest wymagana")
    private Long categoryId;
}


