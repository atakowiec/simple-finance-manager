package pl.pollub.backend.group.dto;

import lombok.Data;
import pl.pollub.backend.group.enums.MembershipStatus;

@Data
public class InviteTargetDto {
    private Long id;
    private String username;
    private MembershipStatus membershipStatus;
}
