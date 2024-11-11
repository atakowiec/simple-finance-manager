package pl.pollub.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for user role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDto {
    private String role;
}
