package pl.pollub.backend.auth.user;

import pl.pollub.backend.auth.dto.UserEmailEditDto;
import pl.pollub.backend.auth.dto.UserPasswordChangeDto;
import pl.pollub.backend.auth.dto.UserRoleDto;
import pl.pollub.backend.auth.dto.UserUsernameEditDto;
import pl.pollub.backend.categories.dto.UserDto;

import java.util.List;

public interface UserService {
    User getUserById(long id);

    User getUserByIdOrThrow(long id);

    boolean emailExists(String email);

    boolean usernameExists(String username);

    String updateUsername(Long userId, UserUsernameEditDto usernameEditDto);

    String updateEmail(Long userId, UserEmailEditDto emailEditDto);

    String updateUserPassword(Long userId, UserPasswordChangeDto passwordChangeDto);

    List<UserDto> getUsers(int page, int size);

    String updateUserRole(Long userId, UserRoleDto roleDto);

    String deleteUser(Long userId);

    UsersRepository getUserRepository();
}
