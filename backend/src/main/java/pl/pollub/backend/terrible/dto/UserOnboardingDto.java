package pl.pollub.backend.terrible.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import pl.pollub.backend.config.constants.ValidationConstants;

/**
 * Data transfer object for creating a user, group, and expense in a single operation.
 * Encapsulates all required data for the comprehensive creation process.
 */
@Data
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

    private UserOnboardingDto(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.groupName = builder.groupName;
        this.expenseLimit = builder.expenseLimit;
        this.expenseName = builder.expenseName;
        this.amount = builder.amount;
        this.categoryId = builder.categoryId;
    }

    public static class Builder {
        private String username;
        private String password;
        private String email;
        private String groupName;
        private double expenseLimit;
        private String expenseName;
        private Double amount;
        private Long categoryId;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder groupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Builder expenseLimit(double expenseLimit) {
            this.expenseLimit = expenseLimit;
            return this;
        }

        public Builder expenseName(String expenseName) {
            this.expenseName = expenseName;
            return this;
        }

        public Builder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public UserOnboardingDto build() {
            return new UserOnboardingDto(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}


