package pl.pollub.backend.incomes;

import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class IncomeService {
    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public List<Income> getAllIncomesForUser(User user) {
        return incomeRepository.findByUser(user);
    }

    public Income addIncome(Income income) {
        return incomeRepository.save(income);
    }

    public Income updateIncome(Long id, Income updatedIncome, User user) {
        return incomeRepository.findByIdAndUser(id, user)
                .map(income -> {
                    income.setName(updatedIncome.getName());
                    income.setAmount(updatedIncome.getAmount());
                    income.setCategory(updatedIncome.getCategory());
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