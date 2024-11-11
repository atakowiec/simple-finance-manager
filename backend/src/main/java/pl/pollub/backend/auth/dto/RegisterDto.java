package pl.pollub.backend.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Data transfer object for registration data.
 */
@Data
public class RegisterDto {
    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Length(min = 3, max = 20, message = "Nazwa użytkownika musi mieć od 3 do 20 znaków")
    private String username;

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Niepoprawny format adresu email")
    private String email;

    @NotNull(message = "Hasło jest wymagane")
    @Length(min = 6, max = 30, message = "Hasło musi mieć od 6 do 30 znaków")
    private String password;
}
