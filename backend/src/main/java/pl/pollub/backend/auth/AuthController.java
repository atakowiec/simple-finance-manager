package pl.pollub.backend.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.backend.auth.dto.LoginDto;
import pl.pollub.backend.auth.dto.RegisterDto;
import pl.pollub.backend.auth.user.User;

/**
 * Controller for managing authentication.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j(topic = "AuthController")
@Tag(name = "Autoryzacja", description = "Zarządzanie autoryzacją")
public class AuthController {
    private final AuthService authService;

    @Operation(description = "Rejestracja nowego użytkownika")
    @ApiResponse(responseCode = "201", description = "Zarejestrowano użytkownika")
    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterDto registerDto, HttpServletResponse res) {
        return authService.handleRegister(registerDto, res);
    }

    @Operation(description = "Logowanie użytkownika")
    @ApiResponse(responseCode = "200", description = "Zalogowano użytkownika")
    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse res) {
        return authService.handleLogin(loginDto, res);
    }

    @Operation(description = "Weryfikacja tokenu użytkownika")
    @ApiResponse(responseCode = "200", description = "Zweryfikowano token poprawnie")
    @PostMapping("/verify")
    public User verify(@AuthenticationPrincipal User user) {
        return user;
    }
}