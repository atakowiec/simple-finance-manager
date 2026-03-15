package pl.pollub.backend.group.observer;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

import java.util.List;

/**
 * Event payload published when group membership changes should trigger notifications.
 */
public record GroupMemberChangeEvent(
        User actor,
        User member,
        Group group,
        GroupMemberChangeAction action,
        List<User> recipients
) {
    public GroupMemberChangeEvent {
        recipients = recipients == null ? List.of() : List.copyOf(recipients);
    }
}

