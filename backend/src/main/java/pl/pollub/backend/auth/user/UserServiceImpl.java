package pl.pollub.backend.auth.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.AuthService;
import pl.pollub.backend.auth.dto.UserEmailEditDto;
import pl.pollub.backend.auth.dto.UserPasswordChangeDto;
import pl.pollub.backend.auth.dto.UserRoleDto;
import pl.pollub.backend.auth.dto.UserUsernameEditDto;
import pl.pollub.backend.auth.user.deletion.UserDeletionMediator;
import pl.pollub.backend.auth.user.memento.UserProfileCaretaker;
import pl.pollub.backend.auth.user.memento.UserProfileMemento;
import pl.pollub.backend.categories.dto.UserDto;
import pl.pollub.backend.exception.HttpException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing users. It provides methods for updating user data.
 */
@Service
@RequiredArgsConstructor
@Getter
public class UserServiceImpl implements UserService {
    @Getter
    private final UsersRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final UserDeletionMediator userDeletionMediator;
    private final UserProfileCaretaker userProfileCaretaker;


    @Override
    public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }


    @Override
    public User getUserByIdOrThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND.value(), "Użytkownik nie znaleziony"));
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public String updateUsername(Long userId, UserUsernameEditDto usernameEditDto) {
        if (usernameExists(usernameEditDto.getUsername())) {
            throw new HttpException(HttpStatus.CONFLICT.value(), "Nazwa użytkownika jest zajęta.");
        }

        User user = getUserByIdOrThrow(userId);
        saveUserProfileState(user);

        user.setUsername(usernameEditDto.getUsername());
        userRepository.save(user);
        return "Nazwa użytkownika została zaktualizowana.";
    }

    @Override
    public String updateEmail(Long userId, UserEmailEditDto emailEditDto) {
        if (emailExists(emailEditDto.getEmail())) {
            throw new HttpException(HttpStatus.CONFLICT.value(), "Adres e-mail jest zajęty.");
        }

        User user = getUserByIdOrThrow(userId);
        saveUserProfileState(user);
        user.setEmail(emailEditDto.getEmail());
        userRepository.save(user);
        return "Email został zaktualizowany";
    }

    @Override
    public String updateUserPassword(Long userId, UserPasswordChangeDto passwordChangeDto) {
        String oldPassword = passwordChangeDto.getOldPassword();
        String newPassword = passwordChangeDto.getNewPassword();

        User user = getUserByIdOrThrow(userId);

        if (!authService.verifyPassword(user.getPassword(), oldPassword)) {
            throw new HttpException(HttpStatus.UNAUTHORIZED.value(), "Hasło jest nieprawidłowe");
        }

        saveUserProfileState(user);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Hasło zostało zaktualizowane.";
    }

    @Override
    public String undoProfileChange(Long userId) {
        User user = getUserByIdOrThrow(userId);
        UserProfileMemento memento = userProfileCaretaker.getLastMemento(userId);

        if (memento == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), "Brak historii zmian profilu do cofnięcia");
        }

        user.setUsername(memento.getUsername());
        user.setEmail(memento.getEmail());
        user.setPassword(memento.getPassword());
        userRepository.save(user);
        return "Cofnięto ostatnią zmianę profilu użytkownika.";
    }

    @Override
    public boolean canUndoProfileChange(Long userId) {
        return userProfileCaretaker.hasHistory(userId);
    }

    public List<UserDto> getUsers(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        return userRepository.findAllPageable(pageable).stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name()))
                .collect(Collectors.toList());
    }

    public String updateUserRole(Long userId, UserRoleDto roleDto) {
        Role role;
        try {
            role = Role.valueOf(roleDto.getRole());
        } catch (IllegalArgumentException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), "Nieprawidłowa rola użytkownika.");
        }

        User user = getUserByIdOrThrow(userId);
        user.setRole(role);
        userRepository.save(user);
        return "Rola użytkownika została zaktualizowana.";
    }

    @Override
    public String deleteUser(Long userId) {
        User user = getUserByIdOrThrow(userId);
        return userDeletionMediator.deleteUser(user);
    }

    private void saveUserProfileState(User user) {
        UserProfileMemento memento = UserProfileMemento.create(
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
        );
        userProfileCaretaker.saveMemento(user.getId(), memento);
    }
}
