package pl.pollub.frontend.manager;

import lombok.Getter;
import lombok.Setter;
import pl.pollub.frontend.user.User;

public class AuthManager {

    @Setter
    @Getter
    private User user;

    public boolean isLoggedIn() {
        return user != null && user.getToken() != null;
    }

    public String getToken() {
        return !isLoggedIn() ? null : user.getToken();
    }

    public boolean isAdmin() {
        return isLoggedIn() && user.isAdmin();
    }
}
