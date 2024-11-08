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

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;
    private final GroupService groupService;

    public List<Income> getAllIncomesForGroup(User user, long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        return incomeRepository.findAllByGroup(group);
    }

    public Income addIncome(Income income) {
        return incomeRepository.save(income);
    }

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

    public void deleteIncome(Long id, User user) {
        if (incomeRepository.existsByIdAndUser(id, user)) {
            incomeRepository.deleteById(id);
        } else {
            throw new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono dochodu z identyfikatorem: " + id);
        }
    }

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
