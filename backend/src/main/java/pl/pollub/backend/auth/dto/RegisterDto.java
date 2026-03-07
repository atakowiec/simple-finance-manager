package pl.pollub.backend.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import pl.pollub.backend.config.constants.ValidationConstants;

/**
 * Data transfer object for registration data.
 */
@Data
public class RegisterDto {
    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Length(min = ValidationConstants.USERNAME_MIN_LENGTH, max = ValidationConstants.USERNAME_MAX_LENGTH, message = "Nazwa użytkownika musi mieć od 3 do 20 znaków")
    private String username;

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Niepoprawny format adresu email")
    private String email;

    @NotNull(message = "Hasło jest wymagane")
    @Length(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH, message = "Hasło musi mieć od 6 do 30 znaków")
    private String password;
}
