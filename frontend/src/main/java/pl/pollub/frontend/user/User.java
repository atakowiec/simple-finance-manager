package pl.pollub.frontend.user;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private boolean isAdmin;
    private String token;
}
