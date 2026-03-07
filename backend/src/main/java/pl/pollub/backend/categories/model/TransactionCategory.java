package pl.pollub.backend.categories.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    public record BasicData(Long id, String name, CategoryType categoryType) {}

    public TransactionCategory(BasicData basicData, byte[] icon) {
        this.id = basicData.id();
        this.name = basicData.name();
        this.categoryType = basicData.categoryType();
        this.icon = icon;
    }

    @Override
    public CategoryDto toDto() {
        List<CategoryDto> childrenDto = children != null ? children.stream()
                .map(TransactionCategory::toDto)
                .toList() : new ArrayList<>();
        return new CategoryDto(id, name, categoryType, childrenDto);
    }
}
