package pl.pollub.backend.group.dto;

import lombok.Data;
import pl.pollub.backend.group.enums.MembershipStatus;

/**
 * DTO for invite target. It holds information about user that can be invited to group.
 */
@Data
public class InviteTargetDto {
    private Long id;
    private String username;
    private MembershipStatus membershipStatus;
}
