package pl.pollub.backend.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer object for editing user email.
 */
@Data
public class UserEmailEditDto {
    @NotBlank(message = "Email nie może być pusty.")
    @Email(message = "Podany email jest nieprawidłowy.")
    private String email;
}
