package pl.pollub.backend.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.model.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository for expenses.
 */
public interface ExpenseRepository extends JpaRepository<Expense, Long>, TransactionRepository<Expense> {
    @Override
    @NonNull
    <S extends Expense> S save(@NonNull S expense);

    @Override
    @NonNull
    void deleteById(@NonNull Long id);

    @Override
    Optional<Expense> findByIdAndUser(Long id, User user);

    @Override
    List<Expense> findAllByGroup(Group group);

    @Override
    @Query("SELECT e.category.id, SUM(e.amount) FROM Expense e WHERE e.group = :group AND e.date >= :minDate GROUP BY e.category")
    List<Object[]> sumAllByGroupAndMinDate(Group group, LocalDate minDate);

    @Override
    void deleteAllByGroup(Group group);

    @Override
    @Query("SELECT e.date, SUM(e.amount) FROM Expense e WHERE e.group = :group AND e.date >= :minDate GROUP BY e.date ORDER BY e.date")
    List<Object[]> findAllByGroupAndMinDateAndGroupByDate(Group group, LocalDate minDate);

    @Override
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.group = :group AND e.date >= :minDate")
    Double getTotalByGroupAndMinDate(Group group, LocalDate minDate);
}