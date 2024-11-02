package pl.pollub.backend.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.dto.LoginDto;
import pl.pollub.backend.auth.dto.RegisterDto;
import pl.pollub.backend.auth.jwt.JwtService;
import pl.pollub.backend.auth.user.Role;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.util.SimpleJsonBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j(topic = "AuthController")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public Map<?, ?> register(@Valid @RequestBody RegisterDto registerDto, HttpServletResponse res) {
        if (authService.isUsernameTaken(registerDto.getUsername())) {
            log.warn("User tried to register with already taken username: {}", registerDto.getUsername());
            throw new HttpException(HttpStatus.CONFLICT, "username");
        }

        if (authService.isEmailTaken(registerDto.getEmail())) {
            log.warn("User tried to register with already taken email: {}", registerDto.getEmail());
            throw new HttpException(HttpStatus.CONFLICT, "email");
        }

        String hashedPassword = authService.hashPassword(registerDto.getPassword());

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(hashedPassword);
        user.setRole(Role.USER);
        authService.getUsersRepository().save(user);

        res.setStatus(201);
        String token = jwtService.createToken(user);
        jwtService.addTokenToResponse(res, token);
        log.info("User registered: {}", user.getUsername());

        return SimpleJsonBuilder.of("id", user.getId())
                .add("username", user.getUsername())
                .add("email", user.getEmail())
                .add("role", user.getRole().name())
                .add("token", token)
                .build();
    }

    @PostMapping("/login")
    public Map<?, ?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse res) {
        User user;

        if (loginDto.getIdentifier().contains("@")) {
            user = authService.getUsersRepository().findByEmail(loginDto.getIdentifier())
                    .orElseThrow(() -> {
                        log.warn("User tried to login with non-existing email: {}", loginDto.getIdentifier());
                        return new HttpException(HttpStatus.UNAUTHORIZED, "Niepoprawne dane logowania");
                    });
        } else {
            user = authService.getUsersRepository().findByUsername(loginDto.getIdentifier())
                    .orElseThrow(() -> {
                        log.warn("User tried to login with non-existing username: {}", loginDto.getIdentifier());
                        return new HttpException(HttpStatus.UNAUTHORIZED, "Niepoprawne dane logowania");
                    });
        }

        if (!authService.verifyPassword(user.getPassword(), loginDto.getPassword())) {
            log.warn("User tried to login with incorrect password: {}", loginDto.getIdentifier());
            throw new HttpException(HttpStatus.UNAUTHORIZED, "Niepoprawne dane logowania");
        }

        String token = jwtService.createToken(user);
        jwtService.addTokenToResponse(res, token);
        log.info("User logged in: {}", user.getUsername());

        return SimpleJsonBuilder.of("id", user.getId())
                .add("username", user.getUsername())
                .add("email", user.getEmail())
                .add("role", user.getRole().name())
                .add("token", token)
                .build();
    }

    @PostMapping("/verify")
    public User verify(@AuthenticationPrincipal User user) {
        return user;
    }
}