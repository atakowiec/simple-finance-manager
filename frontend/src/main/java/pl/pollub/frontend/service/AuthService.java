package pl.pollub.frontend.service;

import com.google.gson.JsonObject;
import lombok.Getter;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;
import pl.pollub.frontend.user.User;
import pl.pollub.frontend.util.JsonUtil;

import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.prefs.Preferences;

@Injectable
public class AuthService {
    private final Preferences preferences = Preferences.userNodeForPackage(AuthService.class);

    @Getter
    private User user;

    @Inject
    private HttpService httpService;

    @Inject
    private ScreenService screenService;

    public void setUser(User user) {
        this.user = user;

        if (user != null) {
            saveToken(user.getToken());
        } else {
            logout();
        }
    }

    public boolean isLoggedIn() {
        return user != null && user.getToken() != null;
    }

    public String getToken() {
        return !isLoggedIn() ? null : user.getToken();
    }

    public boolean isAdmin() {
        return isLoggedIn() && user.isAdmin();
    }

    public boolean tryLogin() {
        String token = preferences.get("token", null);
        if (token == null) {
            return false;
        }

        this.user = new User();
        this.user.setToken(token);

        HttpResponse<String> response = httpService.post("/auth/verify", token);

        if (response.statusCode() != 200) {
            logout();
            return false;
        }

        JsonObject body = JsonUtil.fromJson(response.body()).getAsJsonObject();

        this.user.setUsername(body.get("username").getAsString());
        this.user.setEmail(body.get("email").getAsString());
        this.user.setAdmin(Objects.equals(body.get("role").getAsString(), "ADMIN"));
        this.user.setId(body.get("id").getAsLong());

        return true;
    }

    private void saveToken(String token) {
        preferences.put("token", token);
    }

    public void logout() {
        this.user = null;
        preferences.remove("token");
        this.screenService.switchTo("login");
    }
}
