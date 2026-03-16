package pl.pollub.backend.transaction.amount;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.HttpException;

import java.util.ArrayList;
import java.util.List;

// start interpreter
/**
 * Parses and evaluates amount arithmetic expressions, e.g. "100+100-20".
 */
@Component
public class AmountExpressionInterpreter {

    public double interpret(String rawExpression) {
        AmountExpression expressionTree = parse(rawExpression);
        double result = expressionTree.interpret();
        if (!Double.isFinite(result)) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Kwota jest poza zakresem");
        }

        return result;
    }

    public AmountExpression parse(String rawExpression) {
        if (rawExpression == null || rawExpression.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Kwota jest wymagana");
        }

        String expression = rawExpression.replaceAll("\\s+", "");
        if (expression.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Kwota jest wymagana");
        }

        List<NumberExpression> values = new ArrayList<>();
        List<Character> operators = new ArrayList<>();

        int index = 0;
        boolean expectNumber = true;

        while (index < expression.length()) {
            if (expectNumber) {
                int sign = 1;
                char current = expression.charAt(index);

                if (current == '+' || current == '-') {
                    sign = current == '-' ? -1 : 1;
                    index++;
                    if (index >= expression.length()) {
                        throw new HttpException(HttpStatus.BAD_REQUEST, "Niepoprawne wyrażenie kwoty");
                    }
                }

                int start = index;
                while (index < expression.length() && isPartOfNumber(expression.charAt(index))) {
                    index++;
                }

                if (start == index) {
                    throw new HttpException(HttpStatus.BAD_REQUEST, "Niepoprawne wyrażenie kwoty");
                }

                String numberToken = expression.substring(start, index).replace(',', '.');
                double parsed;
                try {
                    parsed = Double.parseDouble(numberToken);
                } catch (NumberFormatException ex) {
                    throw new HttpException(HttpStatus.BAD_REQUEST, "Niepoprawny format kwoty");
                }

                values.add(new NumberExpression(sign * parsed));
                expectNumber = false;
            } else {
                char operator = expression.charAt(index);
                if (operator != '+' && operator != '-') {
                    throw new HttpException(HttpStatus.BAD_REQUEST, "Dozwolone operatory to + i -");
                }
                operators.add(operator);
                index++;
                expectNumber = true;
            }
        }

        if (expectNumber || values.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Niepoprawne wyrażenie kwoty");
        }

        AmountExpression expressionTree = values.get(0);
        for (int i = 0; i < operators.size(); i++) {
            NumberExpression right = values.get(i + 1);
            expressionTree = operators.get(i) == '+'
                    ? new AddExpression(expressionTree, right)
                    : new SubtractExpression(expressionTree, right);
        }

        return expressionTree;
    }

    private boolean isPartOfNumber(char value) {
        return Character.isDigit(value) || value == '.' || value == ',';
    }
}

