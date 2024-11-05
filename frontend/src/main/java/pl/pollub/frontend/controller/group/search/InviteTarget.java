package pl.pollub.frontend.controller.group.search;

import lombok.Data;
import pl.pollub.frontend.enums.MembershipStatus;

@Data
public class InviteTarget {
    private Long id;
    private String username;
    private MembershipStatus membershipStatus;
}
