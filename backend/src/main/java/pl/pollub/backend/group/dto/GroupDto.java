package pl.pollub.backend.group.dto;

import lombok.Data;

import java.util.List;

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
