package pl.pollub.backend.categories.visitor;

import pl.pollub.backend.categories.dto.CategoryDto;
import pl.pollub.backend.categories.model.TransactionCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts category tree to recursive DTO representation.
 */
public class CategoryToDtoVisitor implements CategoryVisitor<CategoryDto> {

    @Override
    public CategoryDto visit(TransactionCategory category) {
        List<CategoryDto> childrenDto = new ArrayList<>();
        if (category.getChildren() != null) {
            for (TransactionCategory child : category.getChildren()) {
                childrenDto.add(child.accept(this));
            }
        }

        return new CategoryDto(category.getId(), category.getName(), category.getCategoryType(), childrenDto);
    }
}

