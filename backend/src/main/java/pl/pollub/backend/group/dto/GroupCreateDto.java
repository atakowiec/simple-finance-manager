package pl.pollub.backend.group.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * DTO for creating new group. It holds information about group name and color.
 */
@Data
public class GroupCreateDto {
    @NotEmpty(message = "Nazwa nie może być pusta")
    private String name;

    private String color = "#ffffff";
}
