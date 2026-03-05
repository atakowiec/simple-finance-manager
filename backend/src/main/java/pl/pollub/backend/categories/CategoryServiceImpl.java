package pl.pollub.backend.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.categories.dto.CategoryCreateDto;
import pl.pollub.backend.categories.dto.CategoryUpdateDto;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;

import java.util.List;

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

        if (categoryDto.getParentId() != null) {
            TransactionCategory parent = getCategoryByIdOrThrow(categoryDto.getParentId());
            transactionCategory.setParent(parent);
        }

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

        if (categoryUpdateDto.getParentId() != null) {
            if (categoryUpdateDto.getParentId().equals(id)) {
                throw new HttpException(400, "Kategoria nie może być swoim własnym rodzicem.");
            }
            TransactionCategory parent = getCategoryByIdOrThrow(categoryUpdateDto.getParentId());
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        categoryRepository.save(category);
        return "Kategoria została zaktualizowana.";
    }

    @Override
    public String deleteCategory(Long id) {
        TransactionCategory category = getCategoryByIdOrThrow(id);

        // Optional: Check if it has children and decide what to do. 
        // With CascadeType.ALL, children will be deleted too.
        
        categoryRepository.delete(category);
        return "Kategoria została usunięta.";
    }

    @Override
    public List<TransactionCategory> getAllCategories() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getParent() == null)
                .toList();
    }
}
