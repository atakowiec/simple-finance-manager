package pl.pollub.backend.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.dto.UserEmailEditDto;
import pl.pollub.backend.auth.dto.UserRoleDto;
import pl.pollub.backend.auth.dto.UserUsernameEditDto;
import pl.pollub.backend.auth.user.UserService;
import pl.pollub.backend.categories.dto.UserDto;

import java.util.List;

/**
 * Controller for managing users by the administrator.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Zarządzanie użytkownikami przez administratora")
public class AdminController {
    private final UserService userService;

    @Operation(summary = "Pobierz wszystkich użytkowników")
    @ApiResponse(responseCode = "200", description = "Lista użytkowników")
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        List<UserDto> users = userService.getUsers(page, size);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Zmiana nazwy użytkownika")
    @ApiResponse(responseCode = "200", description = "Zmieniono nazwę użytkownika")
    @PutMapping("/{userId}/username")
    public ResponseEntity<String> updateUsername(@PathVariable Long userId, @RequestBody UserUsernameEditDto usernameEditDto) {
        String message = userService.updateUsername(userId, usernameEditDto);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Zmiana emaila użytkownika")
    @ApiResponse(responseCode = "200", description = "Zmieniono email użytkownika")
    @PutMapping("/{userId}/email")
    public ResponseEntity<String> updateEmail(@PathVariable Long userId, @RequestBody UserEmailEditDto emailEditDto) {
        String message = userService.updateEmail(userId, emailEditDto);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Zmiana roli użytkownika")
    @ApiResponse(responseCode = "200", description = "Zmieniono rolę użytkownika")
    @PutMapping("/{userId}/role")
    public ResponseEntity<String> updateRole(@PathVariable Long userId, @RequestBody UserRoleDto roleDto) {
        String message = userService.updateUserRole(userId, roleDto);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Usuń użytkownika")
    @ApiResponse(responseCode = "200", description = "Usunięto użytkownika")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        String message = userService.deleteUser(userId);
        return ResponseEntity.ok(message);
    }
}

