package pl.pollub.backend.incomes;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    boolean existsByIdAndUser(Long id, User user);

    Optional<Income> findByIdAndUser(Long id, User user);

    List<Income> findAllByGroup(Group group);
}