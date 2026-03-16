package pl.pollub.backend.auth.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.dto.UserEmailEditDto;
import pl.pollub.backend.auth.dto.UserPasswordChangeDto;
import pl.pollub.backend.auth.dto.UserRoleDto;
import pl.pollub.backend.auth.dto.UserUsernameEditDto;
import pl.pollub.backend.categories.dto.UserDto;

import java.util.List;

// start Proxy
/**
 * Proxy for UserService. Central place for access control/logging if needed.
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserServiceProxy implements UserService {

    private final UserServiceImpl userService;

    @Override
    public User getUserById(long id) {
        return userService.getUserById(id);
    }

    @Override
    public User getUserByIdOrThrow(long id) {
        return userService.getUserByIdOrThrow(id);
    }

    @Override
    public boolean emailExists(String email) {
        return userService.emailExists(email);
    }

    @Override
    public boolean usernameExists(String username) {
        return userService.usernameExists(username);
    }

    @Override
    public String updateUsername(Long userId, UserUsernameEditDto usernameEditDto) {
        log.info("Updating username for userId={}", userId);
        return userService.updateUsername(userId, usernameEditDto);
    }

    @Override
    public String updateEmail(Long userId, UserEmailEditDto emailEditDto) {
        log.info("Updating email for userId={}", userId);
        return userService.updateEmail(userId, emailEditDto);
    }

    @Override
    public String updateUserPassword(Long userId, UserPasswordChangeDto passwordChangeDto) {
        log.warn("Updating password for userId={}", userId);
        return userService.updateUserPassword(userId, passwordChangeDto);
    }

    @Override
    public String undoProfileChange(Long userId) {
        log.info("Undoing profile change for userId={}", userId);
        return userService.undoProfileChange(userId);
    }

    @Override
    public boolean canUndoProfileChange(Long userId) {
        return userService.canUndoProfileChange(userId);
    }

    @Override
    public List<UserDto> getUsers(int page, int size) {
        log.info("Listing users page={} size={}", page, size);
        return userService.getUsers(page, size);
    }

    @Override
    public String updateUserRole(Long userId, UserRoleDto roleDto) {
        log.warn("Updating role for userId={} role={}", userId, roleDto.getRole());
        return userService.updateUserRole(userId, roleDto);
    }

    @Override
    public String deleteUser(Long userId) {
        log.warn("Deleting user userId={}", userId);
        return userService.deleteUser(userId);
    }

    @Override
    public UsersRepository getUserRepository() {
        return userService.getUserRepository();
    }
}
