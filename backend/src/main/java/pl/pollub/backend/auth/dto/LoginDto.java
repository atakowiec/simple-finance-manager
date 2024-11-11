package pl.pollub.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Data transfer object for login data.
 */
@Data
public class LoginDto {
    @NotBlank(message = "E-mail lub nazwa użytkownika jest wymagany")
    private String identifier;

    @NotEmpty(message = "Hasło jest wymagane")
    private String password;
}