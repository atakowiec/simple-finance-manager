package pl.pollub.backend.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.dto.LoginDto;
import pl.pollub.backend.auth.dto.RegisterDto;
import pl.pollub.backend.auth.jwt.JwtService;
import pl.pollub.backend.auth.user.Role;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UsersRepository;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.util.SimpleJsonBuilder;

/**
 * Service for managing user authentication.
 */
@Service
@Getter
@RequiredArgsConstructor
@Slf4j(topic = "AuthService")
public class AuthServiceImpl implements AuthService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final UsersRepository usersRepository;
    private final JwtService jwtService;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    @Override
    public User getUserById(long id) {
        return usersRepository.findById(id).orElse(null);
    }


    @Override
    public boolean isUsernameTaken(String username) {
        return usersRepository.findByUsername(username).isPresent();
    }


    @Override
    public String hashPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }


    @Override
    public boolean isEmailTaken(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }


    @Override
    public boolean verifyPassword(String hashedPassword, String currentPassword) {
        return bCryptPasswordEncoder.matches(currentPassword, hashedPassword);
    }


    @Override
    public String handleLogin(LoginDto loginDto, HttpServletResponse res) {
        User user = findUserByIdentifier(loginDto.getIdentifier());
        validateLoginPassword(user, loginDto);
        return createLoginResponse(user, res);
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
        if (!verifyPassword(user.getPassword(), loginDto.getPassword())) {
            log.warn("User tried to login with incorrect password: {}", loginDto.getIdentifier());
            throw new HttpException(HttpStatus.UNAUTHORIZED, "Niepoprawne dane logowania");
        }
    }

    private String createLoginResponse(User user, HttpServletResponse res) {
        String token = jwtService.createToken(user);
        jwtService.addTokenToResponse(res, token);
        log.info("User logged in: {}", user.getUsername());

        return SimpleJsonBuilder.of("id", user.getId())
                .add("username", user.getUsername())
                .add("email", user.getEmail())
                .add("role", user.getRole().name())
                .add("token", token)
                .toJson();
    }

    @Override
    public String handleRegister(RegisterDto registerDto, HttpServletResponse res) {
        validateRegistration(registerDto);
        User user = createUser(registerDto);
        return createRegisterResponse(user, res);
    }

    private void validateRegistration(RegisterDto registerDto) {
        if (isUsernameTaken(registerDto.getUsername())) {
            log.warn("User tried to register with already taken username: {}", registerDto.getUsername());
            throw new HttpException(HttpStatus.CONFLICT, "username");
        }

        if (isEmailTaken(registerDto.getEmail())) {
            log.warn("User tried to register with already taken email: {}", registerDto.getEmail());
            throw new HttpException(HttpStatus.CONFLICT, "email");
        }
    }

    private User createUser(RegisterDto registerDto) {
        String hashedPassword = hashPassword(registerDto.getPassword());

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(hashedPassword);
        user.setRole(Role.USER);
        usersRepository.save(user);
        return user;
    }

    private String createRegisterResponse(User user, HttpServletResponse res) {
        res.setStatus(HttpStatus.CREATED.value());
        String token = jwtService.createToken(user);
        jwtService.addTokenToResponse(res, token);
        log.info("User registered: {}", user.getUsername());

        return SimpleJsonBuilder.of("id", user.getId())
                .add("username", user.getUsername())
                .add("email", user.getEmail())
                .add("role", user.getRole().name())
                .add("token", token)
                .toJson();
    }

    @Override
    public void save(User user) {
        usersRepository.save(user);
    }
}
