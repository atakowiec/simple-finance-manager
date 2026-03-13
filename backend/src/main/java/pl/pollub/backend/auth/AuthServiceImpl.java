package pl.pollub.backend.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.dto.RegisterDto;
import pl.pollub.backend.auth.user.Role;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UsersRepository;
/**
 * Service for managing user authentication.
 */
@Service
@Getter
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final UsersRepository usersRepository;

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
    public User createUser(RegisterDto registerDto) {
        String hashedPassword = hashPassword(registerDto.getPassword());

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(hashedPassword);
        user.setRole(Role.USER);
        usersRepository.save(user);
        return user;
    }

    @Override
    public void save(User user) {
        usersRepository.save(user);
    }
}
