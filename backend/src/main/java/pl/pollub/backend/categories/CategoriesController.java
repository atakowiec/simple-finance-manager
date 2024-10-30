package pl.pollub.backend.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;

    @GetMapping("expenses")
    public List<ExpenseCategory> getExpenseCategories() {
        return expenseCategoryRepository.findAll();
    }

    @GetMapping("incomes")
    public List<IncomeCategory> getIncomeCategories() {
        return incomeCategoryRepository.findAll();
    }
}
