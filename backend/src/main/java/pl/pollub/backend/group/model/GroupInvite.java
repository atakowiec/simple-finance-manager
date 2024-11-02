package pl.pollub.backend.group.model;

import jakarta.persistence.*;
import lombok.Data;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.conversion.DtoConvertible;
import pl.pollub.backend.group.dto.GroupInviteDto;
import pl.pollub.backend.group.dto.GroupMemberDto;

import java.time.LocalDateTime;


@Entity
@Table(name = "group_invites")
@Data
public class GroupInvite implements DtoConvertible<GroupInviteDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "invitee_id", nullable = false)
    private User invitee;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public GroupInviteDto toDto() {
        GroupInviteDto dto = new GroupInviteDto();
        dto.setId(id);
        dto.setCreatedAt(createdAt.toString());
        dto.setGroup(group.toDto());
        dto.setInviter(new GroupMemberDto(inviter));
        dto.setInvitee(new GroupMemberDto(invitee));
        return dto;
    }
}


