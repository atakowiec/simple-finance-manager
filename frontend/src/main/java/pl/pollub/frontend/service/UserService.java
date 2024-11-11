package pl.pollub.frontend.service;

import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;
import pl.pollub.frontend.util.JsonUtil;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Injectable
public class UserService {

    @Inject
    private HttpService httpService;

    public HttpResponse<String> updateUsername(String username) {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("username", username);

        String jsonPayload = JsonUtil.toJson(profileData);
        return httpService.put("/user/edit/username", jsonPayload);
    }


    public HttpResponse<String> updateEmail(String email) {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("email", email);

        String jsonPayload = JsonUtil.toJson(profileData);
        return httpService.put("/user/edit/email", jsonPayload);
    }


    public HttpResponse<String> updateUserPassword(String oldPassword, String newPassword) {
        Map<String, Object> passwordData = new HashMap<>();
        passwordData.put("oldPassword", oldPassword);
        passwordData.put("newPassword", newPassword);

        String jsonPayload = JsonUtil.toJson(passwordData);
        return httpService.put("/user/edit/password", jsonPayload);
    }
}
