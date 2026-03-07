package pl.pollub.backend.auth.user.deletion.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.deletion.UserDeletionContext;
import pl.pollub.backend.auth.user.deletion.UserDeletionHandler;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// start single responsibility principle
/**
 * Handler responsible for transferring group ownership and removing user memberships.
 */
@Component
@RequiredArgsConstructor
public class GroupOwnershipTransferHandler implements UserDeletionHandler {
    private final GroupRepository groupRepository;

    @Override
    public void handle(User user, UserDeletionContext context) {
        transferOwnedGroups(user, context);
        removeMemberships(user);
    }

    private void transferOwnedGroups(User deletedUser, UserDeletionContext context) {
        List<Group> ownedGroups = groupRepository.findByOwner_Id(deletedUser.getId());

        for (Group ownedGroup : ownedGroups) {
            User newOwner = ownedGroup.getUsers().stream()
                    .filter(member -> !Objects.equals(member.getId(), deletedUser.getId()))
                    .findFirst()
                    .orElseThrow(() -> new HttpException(409,
                        "Nie można usunąć użytkownika. Grupa " + ownedGroup.getName() +
                        " nie ma innego członka do przejęcia własności."));

            ownedGroup.setOwner(newOwner);
            ownedGroup.getUsers().removeIf(member -> Objects.equals(member.getId(), deletedUser.getId()));
            groupRepository.save(ownedGroup);
            context.setGroupsTransferred(context.getGroupsTransferred() + 1);
        }
    }

    private void removeMemberships(User deletedUser) {
        List<Group> memberGroups = groupRepository.findByUsers_Id(deletedUser.getId());

        for (Group memberGroup : memberGroups) {
            List<User> users = new ArrayList<>(memberGroup.getUsers());
            boolean changed = users.removeIf(member -> Objects.equals(member.getId(), deletedUser.getId()));
            if (changed) {
                memberGroup.setUsers(users);
                groupRepository.save(memberGroup);
            }
        }
    }
}

