package pl.pollub.backend.auth.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.AuthService;
import pl.pollub.backend.auth.dto.UserUsernameEditDto;
import pl.pollub.backend.auth.dto.UserEmailEditDto;
import pl.pollub.backend.auth.dto.UserPasswordChangeDto;
import pl.pollub.backend.auth.dto.UserLimitDto;
import pl.pollub.backend.exception.HttpException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Getter
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;


    public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public String updateUsername(Long userId, UserUsernameEditDto usernameEditDto) {
        if (usernameExists(usernameEditDto.getUsername())) {
            throw new HttpException(409, "Nazwa użytkownika jest zajęta.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpException(404, "Użytkownik nie znaleziony"));
        user.setUsername(usernameEditDto.getUsername());
        userRepository.save(user);
        return "Nazwa użytkownika została zaktualizowana.";
    }


    public String updateEmail(Long userId, UserEmailEditDto emailEditDto) {
        if (emailExists(emailEditDto.getEmail())) {
            throw new HttpException(409, "Adres e-mail jest zajęty.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new HttpException(404, "Użytkownik nie znaleziony"));
        user.setEmail(emailEditDto.getEmail());
        userRepository.save(user);
        return "Email został zaktualizowany";
    }

    public String updateUserPassword(Long userId, UserPasswordChangeDto passwordChangeDto) {
        String oldPassword = passwordChangeDto.getOldPassword();
        String newPassword = passwordChangeDto.getNewPassword();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpException(404, "Użytkownik nie znaleziony"));

        if (!authService.verifyPassword(user.getPassword(), oldPassword)) {
            throw new HttpException(401, "Hasło jest nieprawidłowe");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Hasło zostało zaktualizowane.";
    }

    public String updateSpendingLimit(Long userId, UserLimitDto userEditDto) {
        if (userEditDto.getSpendingLimit() == null || userEditDto.getSpendingLimit() <= 0) {
            throw new HttpException(400, "Limit wydatków musi być większy niż zero.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpException(404, "Użytkownik nie znaleziony"));
        user.setMonthlyLimit(userEditDto.getSpendingLimit());
        userRepository.save(user);
        return "Limit wydatków został zaktualizowany.";
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

}
