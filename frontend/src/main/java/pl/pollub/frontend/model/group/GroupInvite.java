package pl.pollub.frontend.model.group;

import lombok.Data;

@Data
public class GroupInvite {
    private Long id;
    private GroupMember inviter;
    private GroupMember invitee;
    private String createdAt;
    private Group group;
}
