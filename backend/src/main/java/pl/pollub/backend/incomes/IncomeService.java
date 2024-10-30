package pl.pollub.backend.incomes;

import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.incomes.dto.IncomeUpdateDto;
import pl.pollub.backend.categories.IncomeCategory;
import pl.pollub.backend.categories.IncomeCategoryRepository;
import pl.pollub.backend.exception.HttpException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository categoryRepository;

    public IncomeService(IncomeRepository incomeRepository, IncomeCategoryRepository categoryRepository) {
        this.incomeRepository = incomeRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Income> getAllIncomesForUser(User user) {
        return incomeRepository.findByUser(user);
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
                        IncomeCategory category = categoryRepository.findById(updatedIncome.getCategoryId())
                                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + updatedIncome.getCategoryId()));
                        income.setCategory(category);
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
}
