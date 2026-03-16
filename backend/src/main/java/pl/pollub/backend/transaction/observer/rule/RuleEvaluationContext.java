package pl.pollub.backend.transaction.observer.rule;

/**
 * Runtime values used to evaluate a parsed expense limit rule.
 */
public record RuleEvaluationContext(double totalExpenses, double totalIncomes) {
}

