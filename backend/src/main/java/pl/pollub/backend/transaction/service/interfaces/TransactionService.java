package pl.pollub.backend.transaction.service.interfaces;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.dto.TransactionUpdateDto;
import pl.pollub.backend.transaction.factory.TransactionFactory;
import pl.pollub.backend.transaction.factory.TransactionFactoryContext;
import pl.pollub.backend.transaction.model.Transaction;
import pl.pollub.backend.transaction.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// start interface segregation principle
public interface TransactionService<T extends Transaction> {
    GroupService getGroupService();
    CategoryService getCategoryService();
    TransactionRepository<T> getTransactionRepository();
    TransactionFactory<T> getTransactionFactory();

    default T getTransactionByIdAndUserOrThrow(Long id, User user) {
        return getTransactionRepository().findByIdAndUser(id, user)
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono transakcji o podanym identyfikatorze: " + id));
    }

    /**
     * Returns all transations for the specified user in the specified group.
     *
     * @param user    user whose transations will be returned
     * @param groupId group for which the transations will be returned
     * @return list of transations
     */
    default List<T> getAllTransactionsForGroup(User user, long groupId) {
        Group group = getGroupService().getGroupByIdOrThrow(groupId);
        getGroupService().checkMembershipOrThrow(user, group);

        return getTransactionRepository().findAllByGroup(group);
    }


    /**
     * Saves the specified transation to the database.
     *
     * @param transaction transaction to be saved
     * @return saved transation
     */
    default T save(T transaction) {
        return getTransactionRepository().save(transaction);
    }

    /**
     * Updates the transation with the specified id. The transation must belong to the specified user.
     *
     * @param id        id of the transation to be updated
     * @param updateDto updated transation data transfer object
     * @param user      user who owns the transation
     * @return updated transation
     */
    default T updateTransaction(Long id, TransactionUpdateDto updateDto, User user) {
        T transaction = getTransactionByIdAndUserOrThrow(id, user);

        if (updateDto.getName() != null) {
            transaction.setName(updateDto.getName());
        }
        if (updateDto.getAmount() != null) {
            transaction.setAmount(updateDto.getAmount());
        }
        if (updateDto.getCategoryId() != null) {
            TransactionCategory category = getCategoryService().getCategoryByIdOrThrow(updateDto.getCategoryId());
            transaction.setCategory(category);
        }
        if (updateDto.getDate() != null) {
            transaction.setDate(updateDto.getDate());
        }

        return getTransactionRepository().save(transaction);
    }

    /**
     * Deletes the transation with the specified id. The transation must belong to the specified user.
     *
     * @param id   id of the transation to be deleted
     * @param user user who owns the transation
     * @throws HttpException if the transation with the specified id does not exist
     */
    default void deleteTransaction(Long id, User user) {
        T transaction = getTransactionByIdAndUserOrThrow(id, user);

        getGroupService().checkMembershipOrThrow(user, transaction.getGroup());

        getTransactionRepository().deleteById(id);
    }

    /**
     * Returns the total transation for the specified group and month. The transation are grouped by day.
     *
     * @param user    user who requests the statistics
     * @param groupId group for which the statistics will be returned
     * @return total transation for the specified group and month
     */
    default Map<String, Double> getThisMonthStatsByDay(User user, Long groupId) {
        Group group = getGroupService().getGroupByIdOrThrow(groupId);
        getGroupService().checkMembershipOrThrow(user, group);

        LocalDate now = LocalDate.now();
        List<Object[]> result = getTransactionRepository().findAllByGroupAndMinDateAndGroupByDate(group, LocalDate.of(now.getYear(), now.getMonthValue(), 1));

        Map<String, Double> stats = new HashMap<>();

        for (Object[] row : result) {
            stats.compute(row[0].toString(), (k, v) -> v == null ? (Double) row[1] : v + (Double) row[1]);
        }

        return stats;
    }

    /**
     * Returns the total amount of transactions for the specified group in the current month.
     *
     * @param user    user who wants to get the statistics
     * @param groupId group for which the statistics will be returned
     * @return total amount of transactions for the current month
     */
    default Map<String, Double> getThisMonthCategoryStats(User user, Long groupId) {
        Group group = getGroupService().getGroupByIdOrThrow(groupId);
        getGroupService().checkMembershipOrThrow(user, group);

        LocalDate now = LocalDate.now();
        LocalDate minDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1);
        List<Object[]> result = getTransactionRepository().sumAllByGroupAndMinDate(group, minDate);
        Map<Long, Double> rawStats = new HashMap<>();

        for (Object[] row : result)
            rawStats.put((Long) row[0], (Double) row[1]);

        Map<String, Double> stats = new HashMap<>();
        List<TransactionCategory> allCategories = getCategoryService().getAllCategories(); // This now returns only root categories

        for (TransactionCategory root : allCategories) {
            Double total = calculateRecursiveTotal(root, rawStats);
            if (total > 0)
                stats.put(root.getName(), total);
        }

        return stats;
    }

    private Double calculateRecursiveTotal(TransactionCategory category, Map<Long, Double> rawStats) {
        Double total = rawStats.getOrDefault(category.getId(), 0.0);
        if (category.getChildren() != null) {
            for (TransactionCategory child : category.getChildren()) {
                total += calculateRecursiveTotal(child, rawStats);
            }
        }
        return total;
    }

    /**
     * Creates a new transation for the specified user.
     *
     * @param createDto transation data transfer object
     * @param user      user who creates the transation
     * @return created transation
     */
    default T createTransaction(@Valid TransactionCreateDto createDto, User user) {
        TransactionCategory category = getCategoryService().getCategoryByIdOrThrow(createDto.getCategoryId());

        Group group = getGroupService().getGroupByIdOrThrow(createDto.getGroupId());
        getGroupService().checkMembershipOrThrow(user, group);

        // start L1 Factory method
        T transaction = getTransactionFactory().create(createDto, new TransactionFactoryContext(user, category, group));

        return save(transaction);
    }

    /**
     * Clones an existing transaction and saves it as a new entry.
     *
     * @param id   id of the transaction to be cloned
     * @param user user who owns the transaction
     * @return cloned transaction
     */
    default T cloneTransaction(Long id, User user) {
        T original = getTransactionByIdAndUserOrThrow(id, user);

        // Use the Prototype pattern to create a copy
        try {
            @SuppressWarnings("unchecked")
            T cloned = (T) original.clone();

            // Optionally modify the date to today or keep it
            cloned.setDate(LocalDate.now());

            return save(cloned);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning failed", e);
        }
    }
}
