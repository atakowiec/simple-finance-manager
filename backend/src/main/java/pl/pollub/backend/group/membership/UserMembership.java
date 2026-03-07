package pl.pollub.backend.group.membership;

import lombok.Getter;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.enums.MembershipStatus;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.model.GroupInvite;
import pl.pollub.backend.group.membership.state.*;

// start state
/**
 * Context class for group membership state management.
 * Encapsulates the state and delegates behavior to the current state.
 * This class implements the Context role in the State Design Pattern.
 * It maintains the current MemberState and provides methods to perform
 * membership operations, delegating them to the appropriate state.
 */
@Getter
public class UserMembership {

    private MemberState currentState;
    private final User user;
    private final Group group;

    /**
     * Create a new UserMembership with the specified initial state.
     *
     * @param user the user whose membership is being managed
     * @param group the group the user is related to
     * @param initialState the initial membership state
     */
    public UserMembership(User user, Group group, MemberState initialState) {
        this.user = user;
        this.group = group;
        this.currentState = initialState;
    }

    /**
     * Attempt to invite a user to the group.
     * Delegates to the current state to determine if this operation is valid.
     *
     * @param invitee the user to invite
     * @param invite the invitation object
     * @throws HttpException if the operation is invalid for the current state
     */
    public void inviteUser(User invitee, GroupInvite invite) {
        MemberState newState = currentState.invite(invitee, group, invite);
        validateTransition(newState);
        this.currentState = newState;
    }

    /**
     * Attempt to accept an invitation.
     * Delegates to the current state to determine if this operation is valid.
     *
     * @param invite the invitation object to accept
     * @throws HttpException if the operation is invalid for the current state
     */
    public void acceptInvitation(GroupInvite invite) {
        MemberState newState = currentState.accept(user, invite);
        validateTransition(newState);
        // Add user to group members
        group.getUsers().add(user);
        this.currentState = newState;
    }

    /**
     * Attempt to deny an invitation.
     * Delegates to the current state to determine if this operation is valid.
     *
     * @param invite the invitation object to deny
     * @throws HttpException if the operation is invalid for the current state
     */
    public void denyInvitation(GroupInvite invite) {
        MemberState newState = currentState.deny(user, invite);
        validateTransition(newState);
        this.currentState = newState;
    }

    /**
     * Attempt to leave the group.
     * Delegates to the current state to determine if this operation is valid.
     *
     * @throws HttpException if the operation is invalid for the current state
     */
    public void leaveGroup() {
        MemberState newState = currentState.leave(user, group);
        validateTransition(newState);
        this.currentState = newState;
    }

    /**
     * Get the current membership status as an enum value.
     *
     * @return the MembershipStatus corresponding to the current state
     */
    public MembershipStatus getStatus() {
        if (currentState instanceof NoneState) return MembershipStatus.NONE;
        if (currentState instanceof InvitedState) return MembershipStatus.INVITED;
        if (currentState instanceof InGroupState) return MembershipStatus.IN_GROUP;
        throw new IllegalStateException("Unknown state: " + currentState.getClass().getName());
    }

    /**
     * Validate that a state transition is allowed.
     *
     * @param newState the state we're attempting to transition to
     * @throws HttpException if the transition is not allowed
     */
    private void validateTransition(MemberState newState) {
        if (!newState.canTransitionFrom(currentState)) {
            throw new HttpException(409,
                "Invalid state transition from " + currentState.getStatusName() +
                " to " + newState.getStatusName());
        }
    }

    /**
     * Factory method to create a UserMembership with the appropriate initial state.
     * Determines the initial state based on the relationship between user and group.
     *
     * @param user the user whose membership is being determined
     * @param group the group to check membership for
     * @param invite the pending invitation (if any)
     * @return a new UserMembership instance with the appropriate state
     */
    public static UserMembership create(User user, Group group, GroupInvite invite) {
        // If user is already in the group
        if (group.getUsers().contains(user)) {
            return new UserMembership(user, group, InGroupState.INSTANCE);
        }
        // If there's a pending invitation
        else if (invite != null) {
            return new UserMembership(user, group, InvitedState.INSTANCE);
        }
        // Otherwise, no relationship
        else {
            return new UserMembership(user, group, NoneState.INSTANCE);
        }
    }

    @Override
    public String toString() {
        return "UserMembership{" +
                "user=" + user.getId() +
                ", group=" + group.getId() +
                ", state=" + currentState +
                '}';
    }
}

