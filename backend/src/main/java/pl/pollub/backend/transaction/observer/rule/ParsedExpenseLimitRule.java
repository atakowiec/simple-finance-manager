package pl.pollub.backend.transaction.observer.rule;

/**
 * Parsed DSL rule ready for evaluation.
 */
public record ParsedExpenseLimitRule(ExpenseLimitRuleAction action, RuleCondition condition) {
    public boolean matches(RuleEvaluationContext context) {
        return condition.evaluate(context);
    }
}

