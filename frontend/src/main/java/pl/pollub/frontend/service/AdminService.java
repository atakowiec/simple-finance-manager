package pl.pollub.frontend.service;

import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;
import pl.pollub.frontend.model.transaction.TransactionCategory;
import pl.pollub.frontend.user.User;
import pl.pollub.frontend.util.JsonUtil;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Injectable
public class AdminService {

    @Inject
    private HttpService httpService;
    @Inject
    private CategoryService categoryService;

    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() {
        HttpResponse<String> response = httpService.get("/api/admin/users");
        if (response.statusCode() != 200) {
            throw new RuntimeException("Błąd pobierania użytkowników: " + response.statusCode());
        }

        List<User> userList = new ArrayList<>();
        try {
            String responseBody = response.body();
            Map<String, Object>[] usersArray = JsonUtil.GSON.fromJson(responseBody, Map[].class);

            for (Map<String, Object> userMap : usersArray) {
                User user = new User();

                Object idObj = userMap.get("id");
                if (idObj != null) {
                    user.setId(((Number) idObj).longValue());
                } else {
                    user.setId(0);
                }

                user.setUsername((String) userMap.get("username"));
                user.setEmail((String) userMap.get("email"));

                String role = (String) userMap.get("role");
                user.setRole(role);

                user.setAdmin("ADMIN".equals(role));

                Object monthlyLimitObj = userMap.get("monthlyLimit");
                if (monthlyLimitObj != null) {
                    user.setMonthlyLimit(((Number) monthlyLimitObj).doubleValue());
                } else {
                    user.setMonthlyLimit(null);
                }

                userList.add(user);
            }
            return userList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Błąd przy konwersji JSON do listy użytkowników");
        }

    }

    public HttpResponse<String> updateEmail(Long userId, String email) {
        Map<String, String> profileData = new HashMap<>();
        profileData.put("email", email);
        return httpService.put("/api/admin/" + userId + "/email", profileData);
    }

    public HttpResponse<String> updateUsername(Long userId, String username) {
        Map<String, String> profileData = new HashMap<>();
        profileData.put("username", username);
        return httpService.put("/api/admin/" + userId + "/username", profileData);
    }

    public HttpResponse<String> updateLimit(Long userId, Double limit) {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("spendingLimit", limit);
        return httpService.put("/api/admin/" + userId + "/limit", profileData);
    }

    public HttpResponse<String> updateRole(Long userId, String role) {
        Map<String, String> profileData = new HashMap<>();
        profileData.put("role", role);
        return httpService.put("/api/admin/" + userId + "/role", profileData);
    }

    public HttpResponse<String> deleteUser(Long userId) {
        return httpService.delete("/api/admin/" + userId);
    }

    public List<TransactionCategory> getExpenseCategories() {
        return categoryService.getExpenseCategories();
    }

    public List<TransactionCategory> getIncomeCategories() {
        return categoryService.getIncomeCategories();
    }

    public HttpResponse<String> addCategory(TransactionCategory category) {
        return httpService.post("/categories", category);
    }

    public HttpResponse<String> updateCategory(TransactionCategory category) {
        return httpService.put("/categories/" + category.getId(), category);
    }

    public HttpResponse<String> deleteCategory(Long id) {
        return httpService.delete("/categories/" + id);
    }
}
