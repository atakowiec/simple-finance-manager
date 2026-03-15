package pl.pollub.backend.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ActivityLog} entities.
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /** Retrieve all logs belonging to a specific group, newest first. */
    List<ActivityLog> findAllByGroupIdOrderByTimestampDesc(Long groupId);

    /** Retrieve all logs produced by a specific user, newest first. */
    List<ActivityLog> findAllByUser_IdOrderByTimestampDesc(Long userId);
}

