package pl.pollub.backend.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.dto.LoginDto;
import pl.pollub.backend.auth.dto.RegisterDto;
import pl.pollub.backend.auth.jwt.JwtService;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UsersRepository;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.util.SimpleJsonBuilder;

import java.util.Set;

/**
 * Facade for authentication flows (login & register).
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthFacade")
public class AuthFacade {
    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final AuthService authService;

    public String handleLogin(LoginDto loginDto, HttpServletResponse res) {
        User user = findUserByIdentifier(loginDto.getIdentifier());
        validateLoginPassword(user, loginDto);
        return createLoginResponse(user, res);
    }

    public String handleRegister(RegisterDto registerDto, HttpServletResponse res) {
        validateRegistration(registerDto);
        User user = authService.createUser(registerDto);
        return createRegisterResponse(user, res);
    }

    private User findUserByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return usersRepository.findByEmail(identifier)
                    .orElseThrow(() -> {
                        log.warn("User tried to login with non-existing email: {}", identifier);
                        return new HttpException(HttpStatus.UNAUTHORIZED, "Niepoprawne dane logowania");
                    });
        } else {
            return usersRepository.findByUsername(identifier)
                    .orElseThrow(() -> {
                        log.warn("User tried to login with non-existing username: {}", identifier);
                        return new HttpException(HttpStatus.UNAUTHORIZED, "Niepoprawne dane logowania");
                    });
        }
    }

    private void validateLoginPassword(User user, LoginDto loginDto) {
        if (!authService.verifyPassword(user.getPassword(), loginDto.getPassword())) {
            log.warn("User tried to login with incorrect password: {}", loginDto.getIdentifier());
            throw new HttpException(HttpStatus.UNAUTHORIZED, "Niepoprawne dane logowania");
        }
    }

    private String createLoginResponse(User user, HttpServletResponse res) {
        String token = jwtService.createToken(user);
        jwtService.addTokenToResponse(res, token);
        log.info("User logged in: {}", user.getUsername());

        SimpleJsonBuilder builder = SimpleJsonBuilder.of("id", user.getId())
                .add("username", user.getUsername())
                .add("email", user.getEmail())
                .add("role", user.getRole().name())
                .add("token", token);

        log.debug("Login response payload: {}", builder.toRedactedJson(Set.of("email", "token")));
        return builder.toJson();
    }

    private void validateRegistration(RegisterDto registerDto) {
        if (authService.isUsernameTaken(registerDto.getUsername())) {
            log.warn("User tried to register with already taken username: {}", registerDto.getUsername());
            throw new HttpException(HttpStatus.CONFLICT, "username");
        }

        if (authService.isEmailTaken(registerDto.getEmail())) {
            log.warn("User tried to register with already taken email: {}", registerDto.getEmail());
            throw new HttpException(HttpStatus.CONFLICT, "email");
        }
    }

    private String createRegisterResponse(User user, HttpServletResponse res) {
        res.setStatus(HttpStatus.CREATED.value());
        String token = jwtService.createToken(user);
        jwtService.addTokenToResponse(res, token);
        log.info("User registered: {}", user.getUsername());

        SimpleJsonBuilder builder = SimpleJsonBuilder.of("id", user.getId())
                .add("username", user.getUsername())
                .add("email", user.getEmail())
                .add("role", user.getRole().name())
                .add("token", token);

        log.debug("Register response payload: {}", builder.toRedactedJson(Set.of("email", "token")));
        return builder.toJson();
    }
}
