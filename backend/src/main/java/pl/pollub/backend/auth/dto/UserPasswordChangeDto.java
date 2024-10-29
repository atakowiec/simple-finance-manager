package pl.pollub.backend.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Data
public class UserPasswordChangeDto {
    @NotBlank(message = "Obecne hasło jest wymagane.")
    private String oldPassword;

    @NotBlank(message = "Nowe hasło jest wymagane.")
    @Length(min = 6, max = 30, message = "Hasło musi mieć od 6 do 30 znaków.")
    private String newPassword;
}
