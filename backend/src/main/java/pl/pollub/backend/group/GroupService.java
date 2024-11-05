package pl.pollub.backend.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.model.Group;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GroupService {
    @Getter
    private final GroupRepository groupRepository;

    public Group getGroupByIdOrThrow(long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono grupy o podanym identyfikatorze: " + groupId));
    }

    public void checkMembershipOrThrow(User user, Group group) {
        if(group.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new HttpException(HttpStatus.FORBIDDEN, "Nie masz dostępu do tej grupy");
        }
    }

    public List<Group> getAllGroupsForUser(User user) {
        return groupRepository.findByUsers_Id(user.getId());
    }

    public Group createGroup(User user, GroupCreateDto groupCreateDto) {
        Group group = new Group();
        group.setName(groupCreateDto.getName());
        group.setOwner(user);
        group.setColor(groupCreateDto.getColor());
        group.setCreatedAt(LocalDate.now());
        group.setUsers(List.of(user));

        groupRepository.save(group);
        return group;
    }

    public Group changeColor(User user, String color, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        group.setColor(color);

        groupRepository.save(group);

        return group;
    }

    public Group changeName(User user, String newName, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        group.setName(newName);

        groupRepository.save(group);

        return group;
    }

    public Group changeExpenseLimit(User user, Double expenseLimit, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        group.setExpenseLimit(expenseLimit);

        groupRepository.save(group);

        return group;
    }

    public Group deleteMember(User user, Long groupId, Long memberId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        if(!Objects.equals(group.getOwner().getId(), user.getId()))
            throw new HttpException(HttpStatus.FORBIDDEN, "Musisz być właścicielem grupy aby to zrobić!");

        group.getUsers().removeIf(member -> Objects.equals(member.getId(), memberId));

        groupRepository.save(group);

        return null;
    }
}
