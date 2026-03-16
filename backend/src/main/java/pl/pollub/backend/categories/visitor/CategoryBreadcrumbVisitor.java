package pl.pollub.backend.categories.visitor;

import pl.pollub.backend.categories.model.TransactionCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builds breadcrumb path from tree root to selected category.
 */
public class CategoryBreadcrumbVisitor implements CategoryVisitor<List<String>> {

    @Override
    public List<String> visit(TransactionCategory category) {
        List<String> breadcrumb = new ArrayList<>();
        TransactionCategory current = category;

        while (current != null) {
            breadcrumb.add(current.getName());
            current = current.getParent();
        }

        Collections.reverse(breadcrumb);
        return breadcrumb;
    }
}

