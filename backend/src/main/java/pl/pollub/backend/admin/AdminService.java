package pl.pollub.backend.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.dto.*;
import pl.pollub.backend.auth.user.Role;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UserService;
import pl.pollub.backend.categories.ExpenseCategory;
import pl.pollub.backend.categories.IncomeCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.categories.ExpenseCategoryRepository;
import pl.pollub.backend.categories.IncomeCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserService userService;

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;

    public List<AdminDto> getAllUsers() {
        return adminRepository.findAll().stream()
                .map(user -> new AdminDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(), user.getMonthlyLimit()))
                .collect(Collectors.toList());
    }


    public String updateUserUsername(Long userId, UserUsernameEditDto usernameEditDto) {
        return userService.updateUsername(userId, usernameEditDto);
    }


    public String updateUserEmail(Long userId, UserEmailEditDto emailEditDto) {
        return userService.updateEmail(userId, emailEditDto);
    }


    public String updateUserLimit(Long userId, UserLimitDto limitDto) {
        return userService.updateSpendingLimit(userId, limitDto);
    }

    public String updateUserRole(Long userId, UserRoleDto roleDto) {
        Role role;
        try {
            role = Role.valueOf(roleDto.getRole());
        } catch (IllegalArgumentException e) {
            throw new HttpException(400, "Nieprawidłowa rola użytkownika.");
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new HttpException(404, "Użytkownik nie znaleziony"));
        user.setRole(role);
        userService.save(user);
        return "Rola użytkownika została zaktualizowana.";
    }

    public String deleteUser(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new HttpException(404, "Użytkownik nie znaleziony"));

        userService.delete(user);
        return "Użytkownik został pomyślnie usunięty.";
    }

    public String addExpenseCategory(CategoryDto categoryDto) {
        if (expenseCategoryRepository.existsByName(categoryDto.getName())) {
            throw new HttpException(409, "Kategoria o tej nazwie już istnieje.");
        }
        ExpenseCategory expenseCategory = new ExpenseCategory();
        expenseCategory.setName(categoryDto.getName());
        expenseCategory.setIcon(categoryDto.getIcon());
        expenseCategoryRepository.save(expenseCategory);
        return "Kategoria wydatków została dodana pomyślnie.";
    }
    
    public String addIncomeCategory(CategoryDto categoryDto) {
        if (incomeCategoryRepository.existsByName(categoryDto.getName())) {
            throw new HttpException(409, "Kategoria o tej nazwie już istnieje.");
        }
        IncomeCategory incomeCategory = new IncomeCategory();
        incomeCategory.setName(categoryDto.getName());
        incomeCategory.setIcon(categoryDto.getIcon());
        incomeCategoryRepository.save(incomeCategory);
        return "Kategoria przychodów została dodana pomyślnie.";
    }


    public String updateExpenseCategory(Long id, CategoryUpdateDto categoryUpdateDto) {
        ExpenseCategory category = expenseCategoryRepository.findById(id)
                .orElseThrow(() -> new HttpException(404, "Kategoria wydatków nie znaleziona."));

        if (expenseCategoryRepository.existsByName(categoryUpdateDto.getName())) {
            throw new HttpException(409, "Kategoria o tej nazwie już istnieje.");
        }

        category.setName(categoryUpdateDto.getName());
        category.setIcon(categoryUpdateDto.getIcon());
        expenseCategoryRepository.save(category);
        return "Kategoria wydatków została zaktualizowana.";
    }


    public String deleteExpenseCategory(Long id) {
        ExpenseCategory category = expenseCategoryRepository.findById(id)
                .orElseThrow(() -> new HttpException(404, "Kategoria wydatków nie znaleziona."));
        expenseCategoryRepository.delete(category);
        return "Kategoria wydatków została usunięta.";
    }

    public String updateIncomeCategory(Long id, CategoryUpdateDto categoryUpdateDto) {
        IncomeCategory category = incomeCategoryRepository.findById(id)
                .orElseThrow(() -> new HttpException(404, "Kategoria przychodów nie znaleziona."));

        if (incomeCategoryRepository.existsByName(categoryUpdateDto.getName())) {
            throw new HttpException(409, "Kategoria o tej nazwie już istnieje.");
        }

        category.setName(categoryUpdateDto.getName());
        category.setIcon(categoryUpdateDto.getIcon());

        incomeCategoryRepository.save(category);
        return "Kategoria przychodów została zaktualizowana.";
    }


    public String deleteIncomeCategory(Long id) {
        IncomeCategory category = incomeCategoryRepository.findById(id)
                .orElseThrow(() -> new HttpException(404, "Kategoria przychodów nie znaleziona."));
        incomeCategoryRepository.delete(category);
        return "Kategoria przychodów została usunięta.";
    }

}
