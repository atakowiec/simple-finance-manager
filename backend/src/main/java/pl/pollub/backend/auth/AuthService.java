package pl.pollub.backend.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UsersRepository;

@Service
@Getter
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final UsersRepository usersRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean isUsernameTaken(String username) {
        return usersRepository.findByUsername(username).isPresent();
    }

    public String hashPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public boolean isEmailTaken(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    public boolean verifyPassword(String hashedPassword, String currentPassword) {
        return bCryptPasswordEncoder.matches(currentPassword, hashedPassword);
    }
}
