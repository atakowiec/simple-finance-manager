package pl.pollub.backend.auth.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.dto.UserEmailEditDto;
import pl.pollub.backend.auth.dto.UserPasswordChangeDto;
import pl.pollub.backend.auth.dto.UserUsernameEditDto;

/**
 * Controller for managing user by user.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Użytkownik", description = "Zarządzanie użytkownikiem przez użytkownika")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Zmień nazwę użytkownika")
    @ApiResponse(responseCode = "200", description = "Zmieniono nazwę użytkownika")
    @PutMapping("/edit/username")
    @ResponseStatus(HttpStatus.OK)
    public String editUsername(@AuthenticationPrincipal User authenticatedUser,
                               @Valid @RequestBody UserUsernameEditDto usernameEditDto) {
        return userService.updateUsername(authenticatedUser.getId(), usernameEditDto);
    }

    @Operation(summary = "Zmień email użytkownika")
    @ApiResponse(responseCode = "200", description = "Zmieniono email użytkownika")
    @PutMapping("/edit/email")
    @ResponseStatus(HttpStatus.OK)
    public String editEmail(@AuthenticationPrincipal User authenticatedUser,
                            @Valid @RequestBody UserEmailEditDto emailEditDto) {
        return userService.updateEmail(authenticatedUser.getId(), emailEditDto);
    }

    @Operation(summary = "Zmień hasło użytkownika")
    @ApiResponse(responseCode = "200", description = "Zmieniono hasło użytkownika")
    @PutMapping("/edit/password")
    @ResponseStatus(HttpStatus.OK)
    public String editUserPassword(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody UserPasswordChangeDto passwordChangeDto) {

        return userService.updateUserPassword(authenticatedUser.getId(), passwordChangeDto);
    }
}
