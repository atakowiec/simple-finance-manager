package pl.pollub.backend.incomes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    boolean existsByIdAndUser(Long id, User user);

    Optional<Income> findByIdAndUser(Long id, User user);

    List<Income> findAllByGroup(Group group);

    @Query("SELECT i.date, SUM(i.amount) FROM Income i WHERE i.group = :group AND i.date >= :minDate GROUP BY i.date ORDER BY i.date")
    List<Object[]> findAllByGroupAndMinDateAndGroupByDate(Group group, LocalDate minDate);

    void deleteAllByGroup(Group group);
}