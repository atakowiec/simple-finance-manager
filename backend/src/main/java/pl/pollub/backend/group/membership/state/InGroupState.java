package pl.pollub.backend.group.membership.state;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.enums.MembershipStatus;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.model.GroupInvite;

/**
 * Represents "in group" state - user is an active member of the group.
 *
 * Valid operations from IN_GROUP state:
 * - leave(): Transition to NONE state (user leaves group)
 *
 * Invalid operations:
 * - invite(): Cannot invite - already a member
 * - accept(): Cannot accept - already a member
 * - deny(): Cannot deny - already a member
 */
public class InGroupState implements MemberState {

    public static final InGroupState INSTANCE = new InGroupState();

    private InGroupState() {
        // Singleton pattern - prevent instantiation
    }

    @Override
    public MemberState invite(User invitee, Group group, GroupInvite invite) {
        // Invalid: already a member
        throw new HttpException(409, "User is already a member of this group");
    }

    @Override
    public MemberState accept(User user, GroupInvite invite) {
        // Invalid: already a member
        throw new HttpException(409, "User is already a member of this group - cannot accept");
    }

    @Override
    public MemberState deny(User user, GroupInvite invite) {
        // Invalid: already a member
        throw new HttpException(409, "User is already a member of this group - cannot deny");
    }

    @Override
    public MemberState leave(User user, Group group) {
        // Valid transition: IN_GROUP -> NONE
        group.getUsers().remove(user);
        return NoneState.INSTANCE;
    }

    @Override
    public String getStatusName() {
        return MembershipStatus.IN_GROUP.name();
    }

    @Override
    public boolean canTransitionFrom(MemberState previousState) {
        // Can only transition from INVITED state (via accept)
        return previousState instanceof InvitedState;
    }

    @Override
    public String toString() {
        return "InGroupState(active member)";
    }
}

