package pl.pollub.frontend.model.group;

import lombok.Data;

import java.util.List;

@Data
public class Group {
    private Long id;
    private String name;
    private String color;
    private String createdAt;
    private GroupMember owner;
    private List<GroupMember> users;
    private Double expenseLimit;
}
