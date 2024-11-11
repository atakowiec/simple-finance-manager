package pl.pollub.backend.group;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.backend.group.model.Group;

import java.util.List;

/**
 * JPA repository for groups.
 */
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByUsers_Id(Long userId);
}
