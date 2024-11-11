package pl.pollub.backend.group.model;

import jakarta.persistence.*;
import lombok.Data;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.conversion.DtoConvertible;
import pl.pollub.backend.group.dto.GroupDto;
import pl.pollub.backend.group.dto.GroupMemberDto;

import java.time.LocalDate;
import java.util.List;


/**
 * Entity representing group. It holds information about group.
 */
@Entity
@Table(name = "`groups`")
@Data
public class Group implements DtoConvertible<GroupDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "expense_limit")
    private double expenseLimit = -1;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "group_user",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    public GroupDto toDto() {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(id);
        groupDto.setName(name);
        groupDto.setColor(color);
        groupDto.setCreatedAt(createdAt.toString());
        groupDto.setOwner(new GroupMemberDto(owner, owner));
        groupDto.setUsers(users.stream().map((user -> new GroupMemberDto(user, owner))).toList());
        groupDto.setExpenseLimit(expenseLimit);
        return groupDto;
    }
}


