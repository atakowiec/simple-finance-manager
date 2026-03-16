package pl.pollub.backend.categories.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pollub.backend.categories.dto.CategoryDto;
import pl.pollub.backend.categories.visitor.CategoryToDtoVisitor;
import pl.pollub.backend.categories.visitor.CategoryVisitable;
import pl.pollub.backend.categories.visitor.CategoryVisitor;
import pl.pollub.backend.conversion.DtoConvertible;

import java.util.ArrayList;
import java.util.List;

// start composite
/**
 * Entity representing transaction category. It is used to categorize transactions.
 */
@Entity
@Data
@NoArgsConstructor
public class TransactionCategory implements DtoConvertible<CategoryDto>, CategoryVisitable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

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
        return accept(new CategoryToDtoVisitor());
    }

    @Override
    public <T> T accept(CategoryVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
