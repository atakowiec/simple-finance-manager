package pl.pollub.backend.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data transfer object for updating user data.
 */
@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
}
