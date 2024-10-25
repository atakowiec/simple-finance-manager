package pl.pollub.backend.auth.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.dto.UserPasswordChangeDto;
import pl.pollub.backend.auth.dto.UserLimitDto;
import pl.pollub.backend.auth.dto.UserUsernameEditDto;
import pl.pollub.backend.auth.dto.UserEmailEditDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/edit/username")
    @ResponseStatus(HttpStatus.OK)
    public String editUsername(@AuthenticationPrincipal User authenticatedUser,
                               @Valid @RequestBody UserUsernameEditDto usernameEditDto) {
        return userService.updateUsername(authenticatedUser.getId(), usernameEditDto);
    }


    @PutMapping("/edit/email")
    @ResponseStatus(HttpStatus.OK)
    public String editEmail(@AuthenticationPrincipal User authenticatedUser,
                            @Valid @RequestBody UserEmailEditDto emailEditDto) {
        return userService.updateEmail(authenticatedUser.getId(), emailEditDto);
    }

    @PutMapping("/edit/password")
    @ResponseStatus(HttpStatus.OK)
    public String editUserPassword(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody UserPasswordChangeDto passwordChangeDto) {

        return userService.updateUserPassword(authenticatedUser.getId(), passwordChangeDto);
    }


    @PutMapping("/edit/limit")
    @ResponseStatus(HttpStatus.OK)
    public String editUserLimit(@AuthenticationPrincipal User authenticatedUser,
                                @Valid @RequestBody UserLimitDto userLimitDto) {
        return userService.updateSpendingLimit(authenticatedUser.getId(), userLimitDto);
    }

}
