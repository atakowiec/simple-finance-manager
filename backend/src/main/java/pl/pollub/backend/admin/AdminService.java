package pl.pollub.backend.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.dto.UserEmailEditDto;
import pl.pollub.backend.auth.dto.UserLimitDto;
import pl.pollub.backend.auth.dto.UserRoleDto;
import pl.pollub.backend.auth.dto.UserUsernameEditDto;
import pl.pollub.backend.auth.user.Role;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UserService;
import pl.pollub.backend.auth.user.UsersRepository;
import pl.pollub.backend.categories.CategoryRepository;
import pl.pollub.backend.categories.dto.CategoryCreateDto;
import pl.pollub.backend.categories.dto.CategoryUpdateDto;
import pl.pollub.backend.categories.dto.UserDto;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsersRepository usersRepository;
    private final UserService userService;

    private final CategoryRepository categoryRepository;

    public List<UserDto> getAllUsers() {
        return usersRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(), user.getMonthlyLimit()))
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

    public String addCategory(CategoryCreateDto categoryDto) {
        if (categoryRepository.existsByNameAndCategoryType(categoryDto.getName(), categoryDto.getCategoryType())) {
            throw new HttpException(409, "Kategoria o tej nazwie już istnieje.");
        }

        TransactionCategory transactionCategory = new TransactionCategory();
        transactionCategory.setName(categoryDto.getName());
        transactionCategory.setIcon(categoryDto.getIcon());
        transactionCategory.setCategoryType(categoryDto.getCategoryType());
        categoryRepository.save(transactionCategory);

        return "Kategoria została dodana pomyślnie.";
    }


    public String updateCategory(Long id, CategoryUpdateDto categoryUpdateDto) {
        TransactionCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new HttpException(404, "Kategoria wydatków nie znaleziona."));

        TransactionCategory foundCategory = categoryRepository.getByNameAndCategoryType(categoryUpdateDto.getName(), categoryUpdateDto.getCategoryType());

        if (foundCategory != null && !foundCategory.getId().equals(id)) {
            throw new HttpException(409, "Kategoria o tej nazwie już istnieje.");
        }

        category.setName(categoryUpdateDto.getName());

        if (categoryUpdateDto.getIcon() != null)
            category.setIcon(categoryUpdateDto.getIcon());

        categoryRepository.save(category);
        return "Kategoria wydatków została zaktualizowana.";
    }


    public String deleteCategory(Long id) {
        TransactionCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new HttpException(404, "Kategoria wydatków nie znaleziona."));

        categoryRepository.delete(category);
        return "Kategoria wydatków została usunięta.";
    }
}
