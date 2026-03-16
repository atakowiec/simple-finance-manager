package pl.pollub.backend.categories.visitor;

/**
 * Marks model that can be traversed by category visitors.
 */
public interface CategoryVisitable {
    <T> T accept(CategoryVisitor<T> visitor);
}

