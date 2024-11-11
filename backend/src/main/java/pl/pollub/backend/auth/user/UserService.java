package pl.pollub.backend.auth.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.AuthService;
import pl.pollub.backend.auth.dto.UserEmailEditDto;
import pl.pollub.backend.auth.dto.UserPasswordChangeDto;
import pl.pollub.backend.auth.dto.UserUsernameEditDto;
import pl.pollub.backend.exception.HttpException;

import java.util.Optional;

/**
 * Service for managing users. It provides methods for updating user data.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    @Getter
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    /**
     * Returns the user with the specified id or null if the user does not exist.
     *
     * @param id the id of the user.
     * @return the user with the specified id or null if the user does not exist.
     */
    public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Checks if the specified email is already taken.
     *
     * @param email the email to check.
     * @return true if the email is already taken, false otherwise.
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Checks if the specified username is already taken.
     *
     * @param username the username to check.
     * @return true if the username is already taken, false otherwise.
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Updates the username of the user with the specified id.
     *
     * @param userId          the id of the user.
     * @param usernameEditDto the new username.
     * @return a message indicating that the username has been updated.
     */
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

    /**
     * Updates the email of the user with the specified id.
     *
     * @param userId       the id of the user.
     * @param emailEditDto the new email.
     * @return a message indicating that the email has been updated.
     */
    public String updateEmail(Long userId, UserEmailEditDto emailEditDto) {
        if (emailExists(emailEditDto.getEmail())) {
            throw new HttpException(409, "Adres e-mail jest zajęty.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new HttpException(404, "Użytkownik nie znaleziony"));
        user.setEmail(emailEditDto.getEmail());
        userRepository.save(user);
        return "Email został zaktualizowany";
    }

    /**
     * Updates the password of the user with the specified id.
     *
     * @param userId            the id of the user.
     * @param passwordChangeDto the new password data transfer object.
     * @return a message indicating that the password has been updated.
     */
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

    /**
     * Returns the user with the specified id.
     *
     * @param userId the id of the user.
     * @return Optional containing the user with the specified id or an empty Optional if the user does not exist.
     */
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Saves the specified user to the database.
     *
     * @param user the user to save.
     */
    public void save(User user) {
        userRepository.save(user);
    }

    /**
     * Deletes the specified user from the database.
     *
     * @param user the user to delete.
     */
    public void delete(User user) {
        userRepository.delete(user);
    }

}
