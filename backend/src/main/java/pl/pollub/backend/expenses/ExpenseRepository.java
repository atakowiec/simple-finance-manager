package pl.pollub.backend.expenses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    boolean existsByIdAndUser(Long id, User user);

    Optional<Expense> findByIdAndUser(Long id, User user);

    List<Expense> findAllByGroup(Group group);

    @Query("SELECT e.category.id, SUM(e.amount) FROM Expense e WHERE e.group = :group AND e.date >= :minDate GROUP BY e.category")
    List<Object[]> sumAllByGroupAndMinDate(Group group, LocalDate minDate);

    @Query("SELECT e.date, SUM(e.amount) FROM Expense e WHERE e.group = :group AND e.date >= :minDate GROUP BY e.date ORDER BY e.date")
    List<Object[]> findAllByGroupAndMinDateAndGroupByDate(Group group, LocalDate minDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.group = :group AND e.date >= :minDate")
    Double getTotalExpensesByGroupAndMinDate(Group group, LocalDate minDate);
}