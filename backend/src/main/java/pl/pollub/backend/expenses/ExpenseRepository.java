package pl.pollub.backend.expenses;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    boolean existsByIdAndUser(Long id, User user);

    Optional<Expense> findByIdAndUser(Long id, User user);

    List<Expense> findAllByGroup(Group group);
}