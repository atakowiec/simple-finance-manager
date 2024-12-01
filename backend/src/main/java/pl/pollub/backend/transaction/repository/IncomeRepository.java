package pl.pollub.backend.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.model.Income;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository for incomes.
 */
public interface IncomeRepository extends JpaRepository<Income, Long>, TransactionRepository<Income> {
    @Override
    @NonNull
    <S extends Income> S save(@NonNull S income);

    @Override
    @NonNull
    void deleteById(@NonNull Long id);

    @Override
    Optional<Income> findByIdAndUser(Long id, User user);

    @Override
    List<Income> findAllByGroup(Group group);

    @Override
    @Query("SELECT i.date, SUM(i.amount) FROM Income i WHERE i.group = :group AND i.date >= :minDate GROUP BY i.date ORDER BY i.date")
    List<Object[]> findAllByGroupAndMinDateAndGroupByDate(Group group, LocalDate minDate);

    @Override
    void deleteAllByGroup(Group group);

    @Override
    @Query("SELECT e.category.id, SUM(e.amount) FROM Income e WHERE e.group = :group AND e.date >= :minDate GROUP BY e.category")
    List<Object[]> sumAllByGroupAndMinDate(Group group, LocalDate minDate);

    @Override
    @Query("SELECT SUM(e.amount) FROM Income e WHERE e.group = :group AND e.date >= :minDate")
    Double getTotalByGroupAndMinDate(Group group, LocalDate minDate);
}