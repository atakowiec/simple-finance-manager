package pl.pollub.backend.group.membership.state;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.model.GroupInvite;

// start open close principle, Abstrakcja i sterowanie danymi
// start singleton
// start state

/**
 * State interface for group membership.
 * Implements the State Design Pattern to manage membership status transitions
 * and their associated behavior.
 *
 * This interface defines the contract for different membership states:
 * - NONE: User has no relation to the group
 * - INVITED: User has a pending invitation
 * - IN_GROUP: User is an active member
 */
public interface MemberState {

    /**
     * Attempt to invite a user to the group from this state.
     *
     * @param invitee the user to invite
     * @param group the group to invite to
     * @param invite the invitation object
     * @return new state after operation
     * @throws pl.pollub.backend.exception.HttpException if transition is invalid
     */
    MemberState invite(User invitee, Group group, GroupInvite invite);

    /**
     * Attempt to accept an invitation from this state.
     *
     * @param user the user accepting the invitation
     * @param invite the invitation object
     * @return new state after operation
     * @throws pl.pollub.backend.exception.HttpException if transition is invalid
     */
    MemberState accept(User user, GroupInvite invite);

    /**
     * Attempt to deny an invitation from this state.
     *
     * @param user the user denying the invitation
     * @param invite the invitation object
     * @return new state after operation
     * @throws pl.pollub.backend.exception.HttpException if transition is invalid
     */
    MemberState deny(User user, GroupInvite invite);

    /**
     * Attempt to leave the group from this state.
     *
     * @param user the user leaving
     * @param group the group to leave
     * @return new state after operation
     * @throws pl.pollub.backend.exception.HttpException if transition is invalid
     */
    MemberState leave(User user, Group group);

    /**
     * Get the string representation of this state.
     *
     * @return status name matching MembershipStatus enum
     */
    String getStatusName();

    /**
     * Check if transition is allowed from the given previous state to this state.
     *
     * @param previousState the state we're transitioning from
     * @return true if transition is valid, false otherwise
     */
    boolean canTransitionFrom(MemberState previousState);
}

