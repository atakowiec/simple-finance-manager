package pl.pollub.backend.group.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.pollub.backend.auth.user.User;

import java.util.Objects;

@Data
@AllArgsConstructor
public class GroupMemberDto {
    private Long id;
    private String username;
    private boolean isOwner;

    public GroupMemberDto(User user, User owner) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.isOwner = Objects.equals(user.getId(), owner.getId());
    }

    public GroupMemberDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.isOwner = false;
    }
}
