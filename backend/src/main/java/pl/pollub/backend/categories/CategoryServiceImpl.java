package pl.pollub.backend.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.categories.dto.CategoryCreateDto;
import pl.pollub.backend.categories.dto.CategoryUpdateDto;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public TransactionCategory getCategoryByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new HttpException(404, "Kategoria nie znaleziona."));
    }

    @Override
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

    @Override
    public String updateCategory(Long id, CategoryUpdateDto categoryUpdateDto) {
        TransactionCategory category = getCategoryByIdOrThrow(id);

        TransactionCategory foundCategory = categoryRepository.getByNameAndCategoryType(categoryUpdateDto.getName(), categoryUpdateDto.getCategoryType());

        if (foundCategory != null && !foundCategory.getId().equals(id)) {
            throw new HttpException(409, "Kategoria o tej nazwie już istnieje.");
        }

        category.setName(categoryUpdateDto.getName());

        if (categoryUpdateDto.getIcon() != null)
            category.setIcon(categoryUpdateDto.getIcon());

        categoryRepository.save(category);
        return "Kategoria została zaktualizowana.";
    }

    @Override
    public String deleteCategory(Long id) {
        TransactionCategory category = getCategoryByIdOrThrow(id);

        categoryRepository.delete(category);
        return "Kategoria została usunięta.";
    }
}
