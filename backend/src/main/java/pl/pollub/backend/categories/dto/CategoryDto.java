package pl.pollub.backend.categories.dto;

import lombok.Value;
import pl.pollub.backend.categories.model.CategoryType;

/**
 * Data transfer object for category.
 */
@Value
public class CategoryDto {
    Long id;
    String name;
    CategoryType categoryType;
}
