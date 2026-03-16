package pl.pollub.backend.categories.visitor;

import pl.pollub.backend.categories.model.TransactionCategory;

import java.util.Map;

/**
 * Aggregates total amount for category and all its descendants.
 */
public class CategoryTotalAmountVisitor implements CategoryVisitor<Double> {
    private final Map<Long, Double> rawStats;

    public CategoryTotalAmountVisitor(Map<Long, Double> rawStats) {
        this.rawStats = rawStats;
    }

    @Override
    public Double visit(TransactionCategory category) {
        double total = rawStats.getOrDefault(category.getId(), 0.0);
        if (category.getChildren() != null) {
            for (TransactionCategory child : category.getChildren()) {
                total += child.accept(this);
            }
        }

        return total;
    }
}

