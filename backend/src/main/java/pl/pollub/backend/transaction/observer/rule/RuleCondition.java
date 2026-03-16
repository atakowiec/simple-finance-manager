package pl.pollub.backend.transaction.observer.rule;

/**
 * Condition node used by parsed rule AST.
 */
public interface RuleCondition {
    boolean evaluate(RuleEvaluationContext context);
}

