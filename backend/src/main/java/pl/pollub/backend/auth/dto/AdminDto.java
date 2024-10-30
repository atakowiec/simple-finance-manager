package pl.pollub.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Double monthlyLimit;
}
