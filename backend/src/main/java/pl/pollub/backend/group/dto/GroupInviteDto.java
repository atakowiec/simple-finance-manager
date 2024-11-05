package pl.pollub.backend.group.dto;

import lombok.Data;

@Data
public class GroupInviteDto {
    private Long id;
    private GroupMemberDto inviter;
    private GroupMemberDto invitee;
    private String createdAt;
    private GroupDto group;
}
