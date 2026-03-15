package pl.pollub.backend.group.dto;

import lombok.Data;

/**
 * DTO for updating a group's uploaded icon.
 */
@Data
public class GroupIconUpdateDto {
    private byte[] icon;
    private String contentType;
}

