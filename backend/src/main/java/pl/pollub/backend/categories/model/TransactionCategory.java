package pl.pollub.backend.categories.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import pl.pollub.backend.categories.dto.CategoryDto;
import pl.pollub.backend.conversion.DtoConvertible;

import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    // start l2 bridge
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private TransactionCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<TransactionCategory> children = new ArrayList<>();

    @Lob
    @Column(name = "icon", columnDefinition = "LONGBLOB")
    private byte[] icon;

    @Override
    public CategoryDto toDto() {
        List<CategoryDto> childrenDto = children != null ? children.stream()
                .map(TransactionCategory::toDto)
                .toList() : new ArrayList<>();
        return new CategoryDto(id, name, categoryType, childrenDto);
    }
}
