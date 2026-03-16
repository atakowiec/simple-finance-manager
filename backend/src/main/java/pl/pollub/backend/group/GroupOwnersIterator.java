package pl.pollub.backend.group;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.dto.GroupMemberDto;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// start iterator
/**
 * Iterates over group members and returns only owners.
 */
public class GroupOwnersIterator implements Iterator<GroupMemberDto> {
    private final List<User> members;
    private int currentIndex = 0;
    private GroupMemberDto nextOwner;

    public GroupOwnersIterator(List<User> members) {
        this.members = members != null ? members : Collections.emptyList();
        this.nextOwner = findNextOwner();
    }

    @Override
    public boolean hasNext() {
        return nextOwner != null;
    }

    @Override
    public GroupMemberDto next() {
        if (nextOwner == null) {
            throw new NoSuchElementException("No more group owners available");
        }

        GroupMemberDto currentOwner = nextOwner;
        nextOwner = findNextOwner();
        return currentOwner;
    }

    private GroupMemberDto findNextOwner() {
        while (currentIndex < members.size()) {
            User user = members.get(currentIndex++);
            GroupMemberDto member = new GroupMemberDto(user);
            if (member.isOwner()) {
                return member;
            }
        }
        return null;
    }
}

