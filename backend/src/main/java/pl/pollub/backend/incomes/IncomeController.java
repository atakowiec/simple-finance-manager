package pl.pollub.backend.incomes;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping
    public List<Income> getIncomes() {
        return incomeService.getAllIncomes();
    }

    @PostMapping
    public Income createIncome(@RequestBody Income income) {
        return incomeService.addIncome(income);
    }

    @PutMapping("/{id}")
    public Income updateIncome(@PathVariable Long id, @RequestBody Income updatedIncome) {
        return incomeService.updateIncome(id, updatedIncome);
    }

    @DeleteMapping("/{id}")
    public void deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
    }
}
