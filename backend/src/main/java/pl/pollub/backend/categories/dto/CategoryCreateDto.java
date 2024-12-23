package pl.pollub.backend.categories.dto;

import lombok.Data;
import pl.pollub.backend.categories.model.CategoryType;


/**
 * Data transfer object for creating category.
 */
@Data
public class CategoryCreateDto {
    private long id;
    private String name;
    private byte[] icon;
    private CategoryType categoryType;
}
