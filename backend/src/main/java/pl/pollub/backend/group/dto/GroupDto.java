package pl.pollub.backend.group.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO for group. It holds information about group that can be sent back to client.
 */
@Data
public class GroupDto {
    private Long id;
    private String name;
    private String color;
    private String createdAt;
    private GroupMemberDto owner;
    private List<GroupMemberDto> users;
    private double expenseLimit = -1;
}
