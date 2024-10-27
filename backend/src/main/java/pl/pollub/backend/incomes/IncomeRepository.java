package pl.pollub.backend.incomes;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.backend.auth.user.User;

import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUser(User user);

    boolean existsByIdAndUser(Long id, User user);

    Optional<Income> findByIdAndUser(Long id, User user);
}