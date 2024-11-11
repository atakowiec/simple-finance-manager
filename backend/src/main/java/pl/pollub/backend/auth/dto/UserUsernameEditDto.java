package pl.pollub.backend.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

/**
 * Data transfer object for editing user username.
 */
@Data
public class UserUsernameEditDto {

    @NotBlank(message = "Nazwa użytkownika nie może być pusta.")
    @Length(min = 3, max = 20, message = "Nazwa użytkownika musi mieć od 3 do 20 znaków")
    private String username;
}
