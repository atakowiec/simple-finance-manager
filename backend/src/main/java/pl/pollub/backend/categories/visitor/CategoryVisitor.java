package pl.pollub.backend.categories.visitor;

import pl.pollub.backend.categories.model.TransactionCategory;

// start visitor
/**
 * Visitor contract for operations performed on category tree nodes.
 */
public interface CategoryVisitor<T> {
    T visit(TransactionCategory category);
}

