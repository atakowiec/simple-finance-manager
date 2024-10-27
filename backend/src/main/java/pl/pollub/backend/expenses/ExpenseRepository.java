package pl.pollub.backend.expenses;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.backend.auth.user.User;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);

    boolean existsByIdAndUser(Long id, User user);

    Optional<Expense> findByIdAndUser(Long id, User user);
}