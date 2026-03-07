package pl.pollub.backend.group.membership.state;

import org.springframework.http.HttpStatus;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.enums.MembershipStatus;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.model.GroupInvite;

/**
 * Represents "invited" state - user has a pending invitation to the group.
 * Valid operations from INVITED state:
 * - accept(): Transition to IN_GROUP state (user joins group)
 * - deny(): Transition to NONE state (user rejects invitation)
 * Invalid operations:
 * - invite(): Cannot invite again - already invited
 * - leave(): Cannot leave if not yet a member
 */
public class InvitedState implements MemberState {

    public static final InvitedState INSTANCE = new InvitedState();

    private InvitedState() {
        // Singleton pattern - prevent instantiation
    }

    @Override
    public MemberState invite(User invitee, Group group, GroupInvite invite) {
        // Invalid: already invited
        throw new HttpException(HttpStatus.CONFLICT.value(), "User is already invited to this group");
    }

    @Override
    public MemberState accept(User user, GroupInvite invite) {
        // Valid transition: INVITED -> IN_GROUP
        return InGroupState.INSTANCE;
    }

    @Override
    public MemberState deny(User user, GroupInvite invite) {
        // Valid transition: INVITED -> NONE
        return NoneState.INSTANCE;
    }

    @Override
    public MemberState leave(User user, Group group) {
        // Invalid: not yet a member
        throw new HttpException(HttpStatus.CONFLICT.value(), "User is not a member of this group - cannot leave");
    }

    @Override
    public String getStatusName() {
        return MembershipStatus.INVITED.name();
    }

    @Override
    public boolean canTransitionFrom(MemberState previousState) {
        // Can only transition from NONE state (via invite)
        return previousState instanceof NoneState;
    }

    @Override
    public String toString() {
        return "InvitedState(pending invitation)";
    }
}

