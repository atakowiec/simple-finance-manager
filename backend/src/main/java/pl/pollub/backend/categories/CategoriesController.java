package pl.pollub.backend.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.admin.AdminService;
import pl.pollub.backend.auth.dto.CategoryDto;
import pl.pollub.backend.auth.dto.CategoryUpdateDto;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final AdminService adminService;

    @GetMapping("expenses")
    public List<ExpenseCategory> getExpenseCategories() {
        return expenseCategoryRepository.findAll();
    }

    @GetMapping("incomes")
    public List<IncomeCategory> getIncomeCategories() {
        return incomeCategoryRepository.findAll();
    }

    @PostMapping("/expenses")
    public ResponseEntity<String> addExpenseCategory(@RequestBody CategoryDto categoryDto) {
        String message = adminService.addExpenseCategory(categoryDto);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/incomes")
    public ResponseEntity<String> addIncomeCategory(@RequestBody CategoryDto categoryDto) {
        String message = adminService.addIncomeCategory(categoryDto);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<String> updateExpenseCategory(@PathVariable Long id, @RequestBody CategoryUpdateDto categoryUpdateDto) {
        String message = adminService.updateExpenseCategory(id, categoryUpdateDto);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<String> deleteExpenseCategory(@PathVariable Long id) {
        String message = adminService.deleteExpenseCategory(id);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/incomes/{id}")
    public ResponseEntity<String> updateIncomeCategory(@PathVariable Long id, @RequestBody CategoryUpdateDto categoryUpdateDto) {
        String message = adminService.updateIncomeCategory(id, categoryUpdateDto);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/incomes/{id}")
    public ResponseEntity<String> deleteIncomeCategory(@PathVariable Long id) {
        String message = adminService.deleteIncomeCategory(id);
        return ResponseEntity.ok(message);
    }
}
