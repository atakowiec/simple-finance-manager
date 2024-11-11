package pl.pollub.backend.categories.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import pl.pollub.backend.categories.dto.CategoryDto;
import pl.pollub.backend.conversion.DtoConvertible;

/**
 * Entity representing transaction category. It is used to categorize transactions.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCategory implements DtoConvertible<CategoryDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private CategoryType categoryType;

    @Lob
    @Column(name = "icon", columnDefinition = "LONGBLOB")
    private byte[] icon;

    @Override
    public CategoryDto toDto() {
        return new CategoryDto(id, name, categoryType);
    }
}
