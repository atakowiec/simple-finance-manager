package pl.pollub.backend.transaction.repository;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository<T extends Transaction> {
    Optional<T> findByIdAndUser(Long id, User user);

    List<T> findAllByGroup(Group group);

    List<Object[]> findAllByGroupAndMinDateAndGroupByDate(Group group, LocalDate minDate);

    void deleteAllByGroup(Group group);

    List<Object[]> sumAllByGroupAndMinDate(Group group, LocalDate minDate);

    Double getTotalByGroupAndMinDate(Group group, LocalDate minDate);

    <S extends T> S save(S transaction);

    void deleteById(Long id);
}
