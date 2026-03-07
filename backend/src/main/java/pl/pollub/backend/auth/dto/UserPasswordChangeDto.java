package pl.pollub.backend.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import pl.pollub.backend.config.constants.ValidationConstants;

/**
 * Data transfer object for changing user password.
 */
@Data
public class UserPasswordChangeDto {
    @NotBlank(message = "Obecne hasło jest wymagane.")
    private String oldPassword;

    @NotBlank(message = "Nowe hasło jest wymagane.")
    @Length(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH, message = "Hasło musi mieć od 6 do 30 znaków.")
    private String newPassword;
}
