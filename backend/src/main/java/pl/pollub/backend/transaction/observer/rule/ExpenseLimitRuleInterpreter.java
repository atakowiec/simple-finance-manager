package pl.pollub.backend.transaction.observer.rule;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.HttpException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Parses and evaluates the group expense-rule DSL: WHEN <condition> THEN <action>.
 */
@Component
public class ExpenseLimitRuleInterpreter {
    private static final String KEYWORD_WHEN = "WHEN";
    private static final String KEYWORD_THEN = "THEN";

    public ParsedExpenseLimitRule parse(String rawRule) {
        if (rawRule == null || rawRule.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Reguła limitu nie może być pusta");
        }

        String normalized = rawRule.trim();
        String upper = normalized.toUpperCase(Locale.ROOT);

        if (!upper.startsWith(KEYWORD_WHEN + " ")) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Reguła musi zaczynać się od WHEN");
        }

        int thenIndex = upper.indexOf(" " + KEYWORD_THEN + " ");
        if (thenIndex <= KEYWORD_WHEN.length()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Reguła musi mieć składnię: WHEN warunek THEN akcja");
        }

        String conditionText = normalized.substring(KEYWORD_WHEN.length(), thenIndex).trim();
        String actionText = normalized.substring(thenIndex + (" " + KEYWORD_THEN + " ").length()).trim();

        if (conditionText.isBlank() || actionText.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Reguła musi mieć składnię: WHEN warunek THEN akcja");
        }

        if (actionText.contains(" ")) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Akcja reguły musi być pojedynczym tokenem");
        }

        ExpenseLimitRuleAction action;
        try {
            action = ExpenseLimitRuleAction.valueOf(actionText.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Nieznana akcja reguły: " + actionText);
        }

        RuleCondition condition = parseCondition(conditionText);
        return new ParsedExpenseLimitRule(action, condition);
    }

    private RuleCondition parseCondition(String expression) {
        String comparator = findComparator(expression);
        if (comparator == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Warunek musi zawierać porównanie (>, <, >=, <=, ==, !=)");
        }

        int comparatorIndex = expression.indexOf(comparator);
        String leftPart = expression.substring(0, comparatorIndex).trim();
        String rightPart = expression.substring(comparatorIndex + comparator.length()).trim();

        if (leftPart.isBlank() || rightPart.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Niepoprawny warunek reguły");
        }

        NumericExpression left = parseNumericExpression(leftPart);
        NumericExpression right = parseNumericExpression(rightPart);
        return new ComparisonCondition(left, right, comparator);
    }

    private String findComparator(String expression) {
        String[] comparators = {">=", "<=", "==", "!=", ">", "<"};
        for (String comparator : comparators) {
            if (expression.contains(comparator)) {
                return comparator;
            }
        }
        return null;
    }

    private NumericExpression parseNumericExpression(String expression) {
        String compact = expression.replaceAll("\\s+", "");
        if (compact.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Puste wyrażenie arytmetyczne");
        }

        List<NumericExpression> terms = new ArrayList<>();
        List<Character> operators = new ArrayList<>();

        int index = 0;
        boolean expectTerm = true;

        while (index < compact.length()) {
            if (expectTerm) {
                int sign = 1;
                char current = compact.charAt(index);
                if (current == '+' || current == '-') {
                    sign = current == '-' ? -1 : 1;
                    index++;
                    if (index >= compact.length()) {
                        throw new HttpException(HttpStatus.BAD_REQUEST, "Niepoprawne wyrażenie arytmetyczne");
                    }
                }

                int start = index;
                while (index < compact.length() && isTokenCharacter(compact.charAt(index))) {
                    index++;
                }

                if (start == index) {
                    throw new HttpException(HttpStatus.BAD_REQUEST, "Niepoprawne wyrażenie arytmetyczne");
                }

                String token = compact.substring(start, index);
                NumericExpression parsedTerm = parseTerm(token);

                if (sign < 0) {
                    parsedTerm = new SubtractExpression(new NumberExpression(0.0), parsedTerm);
                }

                terms.add(parsedTerm);
                expectTerm = false;
            } else {
                char operator = compact.charAt(index);
                if (operator != '+' && operator != '-') {
                    throw new HttpException(HttpStatus.BAD_REQUEST, "Dozwolone operatory arytmetyczne to + i -");
                }
                operators.add(operator);
                index++;
                expectTerm = true;
            }
        }

        if (expectTerm || terms.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Niepoprawne wyrażenie arytmetyczne");
        }

        NumericExpression tree = terms.get(0);
        for (int i = 0; i < operators.size(); i++) {
            NumericExpression right = terms.get(i + 1);
            tree = operators.get(i) == '+'
                    ? new AddExpression(tree, right)
                    : new SubtractExpression(tree, right);
        }

        return tree;
    }

    private NumericExpression parseTerm(String token) {
        String normalized = token.replace(',', '.');
        if (isNumberToken(normalized)) {
            try {
                return new NumberExpression(Double.parseDouble(normalized));
            } catch (NumberFormatException ex) {
                throw new HttpException(HttpStatus.BAD_REQUEST, "Niepoprawna liczba w regule: " + token);
            }
        }

        String lowered = normalized.toLowerCase(Locale.ROOT);
        if ("totalexpenses".equals(lowered) || "totalexpsenses".equals(lowered)) {
            return new TotalExpensesExpression();
        }

        if ("totalincomes".equals(lowered)) {
            return new TotalIncomesExpression();
        }

        throw new HttpException(HttpStatus.BAD_REQUEST, "Nieznany identyfikator w regule: " + token);
    }

    private boolean isTokenCharacter(char character) {
        return Character.isLetterOrDigit(character) || character == '.' || character == ',' || character == '_';
    }

    private boolean isNumberToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        int dots = 0;
        for (char character : token.toCharArray()) {
            if (character == '.') {
                dots++;
                if (dots > 1) {
                    return false;
                }
                continue;
            }

            if (!Character.isDigit(character)) {
                return false;
            }
        }

        return true;
    }

    private interface NumericExpression {
        double evaluate(RuleEvaluationContext context);
    }

    private record NumberExpression(double value) implements NumericExpression {
        @Override
        public double evaluate(RuleEvaluationContext context) {
            return value;
        }
    }

    private static class TotalExpensesExpression implements NumericExpression {
        @Override
        public double evaluate(RuleEvaluationContext context) {
            return context.totalExpenses();
        }
    }

    private static class TotalIncomesExpression implements NumericExpression {
        @Override
        public double evaluate(RuleEvaluationContext context) {
            return context.totalIncomes();
        }
    }

    private record AddExpression(NumericExpression left, NumericExpression right) implements NumericExpression {
        @Override
        public double evaluate(RuleEvaluationContext context) {
            return left.evaluate(context) + right.evaluate(context);
        }
    }

    private record SubtractExpression(NumericExpression left, NumericExpression right) implements NumericExpression {
        @Override
        public double evaluate(RuleEvaluationContext context) {
            return left.evaluate(context) - right.evaluate(context);
        }
    }

    private record ComparisonCondition(NumericExpression left, NumericExpression right, String comparator) implements RuleCondition {
        @Override
        public boolean evaluate(RuleEvaluationContext context) {
            double leftValue = left.evaluate(context);
            double rightValue = right.evaluate(context);

            return switch (comparator) {
                case ">" -> leftValue > rightValue;
                case "<" -> leftValue < rightValue;
                case ">=" -> leftValue >= rightValue;
                case "<=" -> leftValue <= rightValue;
                case "==" -> Double.compare(leftValue, rightValue) == 0;
                case "!=" -> Double.compare(leftValue, rightValue) != 0;
                default -> throw new HttpException(HttpStatus.BAD_REQUEST, "Nieobsługiwany operator porównania");
            };
        }
    }
}

