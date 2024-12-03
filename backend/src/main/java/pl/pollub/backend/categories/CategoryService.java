package pl.pollub.backend.categories;

import pl.pollub.backend.categories.dto.CategoryCreateDto;
import pl.pollub.backend.categories.dto.CategoryUpdateDto;
import pl.pollub.backend.categories.model.TransactionCategory;

import java.util.List;

public interface CategoryService {
    TransactionCategory getCategoryByIdOrThrow(Long id);

    String addCategory(CategoryCreateDto categoryDto);

    String updateCategory(Long id, CategoryUpdateDto categoryUpdateDto);

    String deleteCategory(Long id);

    List<TransactionCategory> getAllCategories();
}
