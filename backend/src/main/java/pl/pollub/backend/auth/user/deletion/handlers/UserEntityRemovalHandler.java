package pl.pollub.backend.auth.user.deletion.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UsersRepository;
import pl.pollub.backend.auth.user.deletion.UserDeletionContext;
import pl.pollub.backend.auth.user.deletion.UserDeletionHandler;

/**
 * Handler responsible for removing the user entity from the database.
 * This should be the last handler in the deletion workflow.
 */
@Component
@RequiredArgsConstructor
public class UserEntityRemovalHandler implements UserDeletionHandler {
    private final UsersRepository userRepository;

    @Override
    public void handle(User user, UserDeletionContext context) {
        userRepository.delete(user);
    }
}

