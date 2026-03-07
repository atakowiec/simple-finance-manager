package pl.pollub.backend.group.membership.state;

import org.springframework.http.HttpStatus;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.enums.MembershipStatus;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.model.GroupInvite;

/**
 * Represents "no membership" state - user has no relation to the group.
 * Valid operations from NONE state:
 * - invite(): Transition to INVITED state
 *
 * Invalid operations:
 * - accept(): Cannot accept without invitation
 * - deny(): Cannot deny without invitation
 * - leave(): Cannot leave if not a member
 */
public class NoneState implements MemberState {

    public static final NoneState INSTANCE = new NoneState();

    private NoneState() {
        // Singleton pattern - prevent instantiation
    }

    @Override
    public MemberState invite(User invitee, Group group, GroupInvite invite) {
        // Valid transition: NONE -> INVITED
        return InvitedState.INSTANCE;
    }

    @Override
    public MemberState accept(User user, GroupInvite invite) {
        // Invalid: cannot accept from NONE state
        throw new HttpException(HttpStatus.CONFLICT.value(), "Cannot accept invitation from NONE state - no pending invitation");
    }

    @Override
    public MemberState deny(User user, GroupInvite invite) {
        // Invalid: cannot deny from NONE state
        throw new HttpException(HttpStatus.CONFLICT.value(), "Cannot deny invitation from NONE state - no pending invitation");
    }

    @Override
    public MemberState leave(User user, Group group) {
        // Invalid: user is not in group
        throw new HttpException(HttpStatus.CONFLICT.value(), "User is not in this group - cannot leave");
    }

    @Override
    public String getStatusName() {
        return MembershipStatus.NONE.name();
    }

    @Override
    public boolean canTransitionFrom(MemberState previousState) {
        // Can transition from INVITED (deny) or IN_GROUP (leave)
        return previousState instanceof InvitedState ||
               previousState instanceof InGroupState;
    }

    @Override
    public String toString() {
        return "NoneState(no membership)";
    }
}

