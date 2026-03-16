package pl.pollub.backend.group.dto;

import lombok.Data;
import pl.pollub.backend.config.constants.ExpenseLimitConstants;

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
    private boolean hasIcon;
    private String iconChecksum;
    private GroupMemberDto owner;
    private List<GroupMemberDto> users;
    private double expenseLimit = ExpenseLimitConstants.NO_EXPENSE_LIMIT;
    private String expenseLimitRule;
}
