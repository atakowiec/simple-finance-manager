package pl.pollub.backend.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UsersRepository;

/**
 * Service for managing user authentication.
 */
@Service
@Getter
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final UsersRepository usersRepository;

    /**
     * Loads user by username. Throws an exception if the user is not found.
     *
     * @param username the username identifying the user whose data is required.
     * @return the user with the specified username.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Returns the user with the specified id or null if the user does not exist.
     *
     * @param id the id of the user.
     * @return the user with the specified id or null if the user does not exist.
     */
    public User getUserById(long id) {
        return usersRepository.findById(id).orElse(null);
    }

    /**
     * Checks if the specified username is already taken.
     *
     * @param username the username to check.
     * @return true if the username is already taken, false otherwise.
     */
    public boolean isUsernameTaken(String username) {
        return usersRepository.findByUsername(username).isPresent();
    }

    /**
     * Hashes the specified password.
     *
     * @param password the password to hash.
     * @return the hashed password.
     */
    public String hashPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    /**
     * Checks if the specified email is already taken.
     *
     * @param email the email to check.
     * @return true if the email is already taken, false otherwise.
     */
    public boolean isEmailTaken(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    /**
     * Verifies the specified password
     *
     * @param hashedPassword  the hashed password.
     * @param currentPassword the password to verify.
     * @return true if the password is correct, false otherwise.
     */
    public boolean verifyPassword(String hashedPassword, String currentPassword) {
        return bCryptPasswordEncoder.matches(currentPassword, hashedPassword);
    }
}
