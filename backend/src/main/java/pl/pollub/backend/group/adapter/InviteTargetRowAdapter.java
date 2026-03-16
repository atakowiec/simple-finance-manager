package pl.pollub.backend.group.adapter;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.dto.InviteTargetDto;
import pl.pollub.backend.group.enums.MembershipStatus;
import pl.pollub.backend.group.model.Group;

// start adapter
/**
 * Adapter that converts raw query results into InviteTargetDto.
 */
public class InviteTargetRowAdapter {

    public InviteTargetDto adapt(Object[] row, Group group) {
        if (row == null || row.length < 2) {
            throw new IllegalArgumentException("Row must contain user and invite flag");
        }
        User inviteeUser = (User) row[0];
        boolean isInvited = (boolean) row[1];
        return adapt(inviteeUser, isInvited, group);
    }

    public InviteTargetDto adapt(User inviteeUser, boolean isInvited, Group group) {
        InviteTargetDto dto = new InviteTargetDto();
        dto.setId(inviteeUser.getId());
        dto.setUsername(inviteeUser.getUsername());
        dto.setMembershipStatus(determineMembershipStatus(group, inviteeUser, isInvited));
        return dto;
    }

    private MembershipStatus determineMembershipStatus(Group group, User inviteeUser, boolean isInvited) {
        if (group.getUsers().stream().anyMatch(inviteeUser::equals)) {
            return MembershipStatus.IN_GROUP;
        }
        return isInvited ? MembershipStatus.INVITED : MembershipStatus.NONE;
    }
}
