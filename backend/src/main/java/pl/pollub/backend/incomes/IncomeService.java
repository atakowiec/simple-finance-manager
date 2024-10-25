package pl.pollub.backend.incomes;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IncomeService {
    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public List<Income> getAllIncomes() {
        return incomeRepository.findAll();
    }

    public Income addIncome(Income income) {
        return incomeRepository.save(income);
    }

    public Income updateIncome(Long id, Income updatedIncome) {
        return incomeRepository.findById(id)
                .map(income -> {
                    income.setName(updatedIncome.getName());
                    income.setAmount(updatedIncome.getAmount());
                    income.setCategory(updatedIncome.getCategory());
                    return incomeRepository.save(income);
                })
                .orElseThrow(() -> new RuntimeException("Nie znaleziono dochodu z identyfikatorem: " + id));
    }

    public void deleteIncome(Long id) {
        if (incomeRepository.existsById(id)) {
            incomeRepository.deleteById(id);
        } else {
            throw new RuntimeException("Nie znaleziono dochodu z identyfikatorem: " + id);
        }
    }
}
