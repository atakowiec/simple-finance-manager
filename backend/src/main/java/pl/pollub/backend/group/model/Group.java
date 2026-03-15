package pl.pollub.backend.group.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.config.constants.ExpenseLimitConstants;
import pl.pollub.backend.conversion.DtoConvertible;
import pl.pollub.backend.group.dto.GroupDto;
import pl.pollub.backend.group.dto.GroupMemberDto;
import pl.pollub.backend.transaction.observer.state.ExpenseLimitLifecycleStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Entity representing group. It holds information about group.
 */
@Entity
@Table(name = "`groups`")
@Data
public class Group implements DtoConvertible<GroupDto>, Cloneable {
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
    private double expenseLimit = ExpenseLimitConstants.NO_EXPENSE_LIMIT;

    @Enumerated(EnumType.STRING)
    @Column(name = "expense_limit_lifecycle_status", nullable = false)
    private ExpenseLimitLifecycleStatus expenseLimitLifecycleStatus = ExpenseLimitLifecycleStatus.NO_LIMIT;

    @Column(name = "expense_limit_state_month_start")
    private LocalDate expenseLimitStateMonthStart;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    @Column(name = "icon", columnDefinition = "LONGBLOB")
    private byte[] icon;

    @Column(name = "icon_content_type")
    private String iconContentType;

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
        groupDto.setCreatedAt(createdAt != null ? createdAt.toString() : null);
        groupDto.setHasIcon(hasIcon());
        groupDto.setIconChecksum(getIconChecksum());
        groupDto.setOwner(new GroupMemberDto(owner, owner));
        groupDto.setUsers(users.stream().map((user -> new GroupMemberDto(user, owner))).toList());
        groupDto.setExpenseLimit(expenseLimit);
        return groupDto;
    }

    public boolean hasIcon() {
        return icon != null && icon.length > 0;
    }

    public String getIconChecksum() {
        if (!hasIcon()) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(icon);
            StringBuilder builder = new StringBuilder(hash.length * 2);
            for (byte value : hash) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            return String.valueOf(Arrays.hashCode(icon));
        }
    }

    @Override
    public Group clone() {
        try {
            Group cloned = (Group) super.clone();
            cloned.setId(null);
            if (cloned.getUsers() != null) {
                cloned.setUsers(List.copyOf(cloned.getUsers()));
            }
            if (cloned.getIcon() != null) {
                cloned.setIcon(Arrays.copyOf(cloned.getIcon(), cloned.getIcon().length));
            }
            cloned.setExpenseLimitLifecycleStatus(this.expenseLimitLifecycleStatus);
            cloned.setExpenseLimitStateMonthStart(this.expenseLimitStateMonthStart);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning group failed", e);
        }
    }
}


