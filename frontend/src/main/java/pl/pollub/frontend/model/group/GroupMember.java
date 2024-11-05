package pl.pollub.frontend.model.group;

import lombok.Data;

@Data
public class GroupMember {
    private Long id;
    private String username;
    private boolean owner;
}
