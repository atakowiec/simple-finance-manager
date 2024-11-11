package pl.pollub.frontend.user;

import lombok.Data;

@Data
public class User {
    private long id;
    private String username;
    private String email;
    private boolean isAdmin;
    private String token;

    private String role;

    public String getRole() {
        return isAdmin ? "ADMIN" : "USER";
    }
}
