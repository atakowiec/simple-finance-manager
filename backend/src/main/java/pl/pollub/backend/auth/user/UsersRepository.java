package pl.pollub.backend.auth.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Jpa repository for User model
 */
public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u")
    List<User> findAllPageable(Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u, " +
           "CASE WHEN gi.id IS NOT NULL THEN true ELSE false END AS hasActiveInvite " +
           "FROM User u " +
           "LEFT JOIN GroupInvite gi ON gi.invitee = u AND gi.group.id = :groupId " +
           "WHERE u.username LIKE %:username%")
    List<Object[]> findUsersByNicknameWithInviteStatus(@Param("username") String username, @Param("groupId") Long groupId, Pageable pageable);
}
