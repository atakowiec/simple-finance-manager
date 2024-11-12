package pl.pollub.backend.incomes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryRepository;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.incomes.dto.IncomeUpdateDto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing incomes. It provides methods for adding, updating and deleting incomes.
 */
@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;
    private final GroupService groupService;

    /**
     * Returns all incomes for the specified user in the specified group.
     *
     * @param user    user whose incomes will be returned
     * @param groupId group for which the incomes will be returned
     * @return list of incomes
     */
    public List<Income> getAllIncomesForGroup(User user, long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        return incomeRepository.findAllByGroup(group);
    }

    /**
     * Saves the specified income to the database.
     *
     * @param income income to be saved
     * @return saved income
     */
    public Income addIncome(Income income) {
        return incomeRepository.save(income);
    }

    /**
     * Updates the income with the specified id. The income must belong to the specified user.
     *
     * @param id            id of the income to be updated
     * @param updatedIncome updated income data transfer object
     * @param user          user who owns the income
     * @return updated income
     */
    public Income updateIncome(Long id, IncomeUpdateDto updatedIncome, User user) {
        return incomeRepository.findByIdAndUser(id, user)
                .map(income -> {
                    if (updatedIncome.getName() != null) {
                        income.setName(updatedIncome.getName());
                    }
                    if (updatedIncome.getAmount() != null) {
                        income.setAmount(updatedIncome.getAmount());
                    }
                    if (updatedIncome.getCategoryId() != null) {
                        TransactionCategory category = categoryRepository.findById(updatedIncome.getCategoryId())
                                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + updatedIncome.getCategoryId()));
                        income.setCategory(category);
                    }
                    if (updatedIncome.getDate() != null) {
                        income.setDate(updatedIncome.getDate());
                    }
                    return incomeRepository.save(income);
                })
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono dochodu z identyfikatorem: " + id));
    }

    /**
     * Deletes the income with the specified id. The income must belong to the specified user.
     *
     * @param id   id of the income to be deleted
     * @param user user who owns the income
     * @throws HttpException if the income with the specified id does not exist
     */
    public void deleteIncome(Long id, User user) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono przychodu o podanym identyfikatorze: " + id));

        groupService.checkMembershipOrThrow(user, income.getGroup());

        incomeRepository.deleteById(id);
    }

    /**
     * Returns the total income for the specified group and month. The income are grouped by day.
     *
     * @param user    user who requests the statistics
     * @param groupId group for which the statistics will be returned
     * @return total income for the specified group and month
     */
    public Map<String, Double> getThisMonthStatsByDay(User user, Long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        LocalDate now = LocalDate.now();
        List<Object[]> result = incomeRepository.findAllByGroupAndMinDateAndGroupByDate(group, LocalDate.of(now.getYear(), now.getMonthValue(), 1));

        Map<String, Double> stats = new HashMap<>();

        for (Object[] row : result) {
            stats.compute(row[0].toString(), (k, v) -> v == null ? (Double) row[1] : v + (Double) row[1]);
        }

        return stats;
    }
}
