package pl.pollub.backend.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.pollub.backend.auth.dto.LoginDto;
import pl.pollub.backend.auth.dto.RegisterDto;
import pl.pollub.backend.auth.user.User;

public interface AuthService extends UserDetailsService {

    /**
     * Loads user by username. Throws an exception if the user is not found.
     *
     * @param username the username identifying the user whose data is required.
     * @return the user with the specified username.
     * @throws UsernameNotFoundException if the user is not found.
     */
    User loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Returns the user with the specified id or null if the user does not exist.
     *
     * @param id the id of the user.
     * @return the user with the specified id or null if the user does not exist.
     */
    User getUserById(long id);

    /**
     * Checks if the specified username is already taken.
     *
     * @param username the username to check.
     * @return true if the username is already taken, false otherwise.
     */
    boolean isUsernameTaken(String username);

    /**
     * Hashes the specified password.
     *
     * @param password the password to hash.
     * @return the hashed password.
     */
    String hashPassword(String password);

    /**
     * Checks if the specified email is already taken.
     *
     * @param email the email to check.
     * @return true if the email is already taken, false otherwise.
     */
    boolean isEmailTaken(String email);

    /**
     * Verifies the specified password
     *
     * @param hashedPassword  the hashed password.
     * @param currentPassword the password to verify.
     * @return true if the password is correct, false otherwise.
     */
    boolean verifyPassword(String hashedPassword, String currentPassword);

    /**
     * Handles the login request.
     *
     * @param loginDto the login data.
     * @param res      the response.
     * @return the json response containing the token and user data.
     */
    String handleLogin(LoginDto loginDto, HttpServletResponse res);

    /**
     * Handles the registration request.
     *
     * @param registerDto the registration data.
     * @param res         the response.
     * @return the json response containing the token and user data.
     */
    String handleRegister(RegisterDto registerDto, HttpServletResponse res);

    void save(User user);
}
