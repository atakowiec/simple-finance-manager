package pl.pollub.backend.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.backend.categories.model.CategoryType;
import pl.pollub.backend.categories.model.TransactionCategory;

/**
 * JPA repository for transaction categories.
 */
public interface CategoryRepository extends JpaRepository<TransactionCategory, Long> {
    TransactionCategory getByNameAndCategoryType(String name, CategoryType categoryType);

    boolean existsByNameAndCategoryType(String name, CategoryType categoryType);
}
