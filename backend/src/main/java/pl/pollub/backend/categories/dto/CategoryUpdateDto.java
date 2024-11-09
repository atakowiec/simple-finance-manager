package pl.pollub.backend.categories.dto;

import lombok.Data;
import pl.pollub.backend.categories.model.CategoryType;

@Data
public class CategoryUpdateDto {
    private Long id;
    private String name;
    private CategoryType categoryType;
    private byte[] icon;
}
