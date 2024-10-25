package pl.pollub.frontend.service;

import lombok.Getter;
import lombok.Setter;
import pl.pollub.frontend.injector.Injectable;
import pl.pollub.frontend.user.User;

@Injectable
public class AuthService {

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
