package pl.pollub.backend.auth.user.memento;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Snapshot of mutable user profile fields used by undo operation.
 */
@Data
@AllArgsConstructor
public class UserProfileMemento {
    private final String username;
    private final String email;
    private final String password;

    public static UserProfileMemento create(String username, String email, String password) {
        return new UserProfileMemento(username, email, password);
    }
}

