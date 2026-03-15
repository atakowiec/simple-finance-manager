package pl.pollub.backend.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pollub.backend.categories.dto.CategoryCreateDto;
import pl.pollub.backend.categories.dto.CategoryUpdateDto;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.categories.memento.CategoryCaretaker;
import pl.pollub.backend.categories.memento.CategoryMemento;
import pl.pollub.backend.exception.HttpException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final IconFlyweightFactory iconFlyweightFactory;
    private final CategoryCaretaker categoryCaretaker;

    @Override
    public TransactionCategory getCategoryByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND.value(), "Kategoria nie znaleziona."));
    }

    @Override
    public String addCategory(CategoryCreateDto categoryDto) {
        if (categoryRepository.existsByNameAndCategoryType(categoryDto.getName(), categoryDto.getCategoryType())) {
            throw new HttpException(HttpStatus.CONFLICT.value(), "Kategoria o tej nazwie już istnieje.");
        }

        TransactionCategory transactionCategory = new TransactionCategory();
        transactionCategory.setName(categoryDto.getName());
        // use flyweight to store shared icon instance
        transactionCategory.setIcon(iconFlyweightFactory.getOrAdd(categoryDto.getIcon()));
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
        TransactionCategory newParent = null;

        if (foundCategory != null && !foundCategory.getId().equals(id))
            throw new HttpException(HttpStatus.CONFLICT.value(), "Kategoria o tej nazwie już istnieje.");

        if (categoryUpdateDto.getParentId() != null) {
            if (categoryUpdateDto.getParentId().equals(id))
                throw new HttpException(HttpStatus.BAD_REQUEST.value(), "Kategoria nie może być swoim własnym rodzicem.");

            newParent = getCategoryByIdOrThrow(categoryUpdateDto.getParentId());
        }

        saveCategoryState(category);

        category.setName(categoryUpdateDto.getName());
        category.setCategoryType(categoryUpdateDto.getCategoryType());

        if (categoryUpdateDto.getIcon() != null)
            category.setIcon(iconFlyweightFactory.getOrAdd(categoryUpdateDto.getIcon()));

        category.setParent(newParent);
        categoryRepository.save(category);
        return "Kategoria została zaktualizowana.";
    }

    @Override
    public String undoCategoryChange(Long id) {
        TransactionCategory category = getCategoryByIdOrThrow(id);
        CategoryMemento memento = categoryCaretaker.getLastMemento(id);

        if (memento == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), "Brak historii zmian kategorii do cofnięcia");
        }

        category.setName(memento.getName());
        category.setCategoryType(memento.getCategoryType());

        if (memento.getIcon() != null) {
            category.setIcon(iconFlyweightFactory.getOrAdd(memento.getIcon()));
        } else {
            category.setIcon(null);
        }

        if (memento.getParentId() != null) {
            TransactionCategory parent = getCategoryByIdOrThrow(memento.getParentId());
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        categoryRepository.save(category);
        return "Cofnięto ostatnią zmianę kategorii.";
    }

    @Override
    public boolean canUndoCategory(Long id) {
        return categoryCaretaker.hasHistory(id);
    }

    @Override
    public String deleteCategory(Long id) {
        TransactionCategory category = getCategoryByIdOrThrow(id);

        // Optional: Check if it has children and decide what to do. 
        // With CascadeType.ALL, children will be deleted too.
        
        categoryRepository.delete(category);
        categoryCaretaker.clearHistory(id);
        return "Kategoria została usunięta.";
    }

    @Override
    public List<TransactionCategory> getAllCategories() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getParent() == null)
                .toList();
    }

    private void saveCategoryState(TransactionCategory category) {
        CategoryMemento memento = CategoryMemento.create(
                category.getName(),
                category.getCategoryType(),
                category.getIcon(),
                category.getParent() == null ? null : category.getParent().getId()
        );
        categoryCaretaker.saveMemento(category.getId(), memento);
    }
}
