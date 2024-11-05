package pl.pollub.backend.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.model.GroupInvite;

import java.util.List;

@Service
public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {

    GroupInvite findGroupInviteByInviteeAndGroup(User invitee, Group group);

    List<GroupInvite> findAllByInvitee(User user);
}
