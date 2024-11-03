package pl.pollub.backend.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pollub.backend.auth.user.User;

@Repository
public interface AdminRepository extends JpaRepository<User, Long> {

}
