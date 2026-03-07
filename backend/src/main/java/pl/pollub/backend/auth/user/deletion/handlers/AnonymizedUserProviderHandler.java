package pl.pollub.backend.auth.user.deletion.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.Role;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UsersRepository;
import pl.pollub.backend.auth.user.deletion.UserDeletionContext;
import pl.pollub.backend.auth.user.deletion.UserDeletionHandler;

/**
 * Handler responsible for creating or retrieving the anonymized user account.
 */
@Component
@RequiredArgsConstructor
public class AnonymizedUserProviderHandler implements UserDeletionHandler {
    private static final String ANONYMIZED_USERNAME = "deleted-user";
    private static final String ANONYMIZED_EMAIL_BASE = "deleted-user@anonymized.local";

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void handle(User user, UserDeletionContext context) {
        User anonymizedUser = getOrCreateAnonymizedUser();
        context.setAnonymizedUser(anonymizedUser);
    }

    private User getOrCreateAnonymizedUser() {
        return userRepository.findByUsername(ANONYMIZED_USERNAME)
                .orElseGet(() -> {
                    User anonymized = new User();
                    anonymized.setUsername(ANONYMIZED_USERNAME);
                    anonymized.setEmail(generateUniqueAnonymizedEmail());
                    anonymized.setRole(Role.USER);
                    anonymized.setPassword(passwordEncoder.encode("system-anonymized-user"));
                    return userRepository.save(anonymized);
                });
    }

    private String generateUniqueAnonymizedEmail() {
        if (!userRepository.existsByEmail(ANONYMIZED_EMAIL_BASE)) {
            return ANONYMIZED_EMAIL_BASE;
        }

        int suffix = 1;
        String candidate = "deleted-user+" + suffix + "@anonymized.local";
        while (userRepository.existsByEmail(candidate)) {
            suffix++;
            candidate = "deleted-user+" + suffix + "@anonymized.local";
        }
        return candidate;
    }
}

