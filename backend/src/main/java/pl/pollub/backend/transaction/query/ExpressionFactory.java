package pl.pollub.backend.transaction.query;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.HttpException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// start factory
/**
 * Simple Factory for creating Expression objects based on field/operator/value.
 */
@Component
public class ExpressionFactory {

    public Expression create(String field, String operator, String value) {
        switch (field.toLowerCase()) {
            case "amount":
                double amount = parseDouble(value);
                if (operator.equals(">")) {
                    return new AmountGreaterThanExpression(amount);
                } else if (operator.equals("<")) {
                    return new AmountLessThanExpression(amount);
                } else {
                    throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid operator for amount: " + operator + ". Use > or <");
                }

            case "category":
                if (operator.equals("=")) {
                    return new CategoryEqualsExpression(unquote(value));
                } else {
                    throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid operator for category: " + operator + ". Use =");
                }

            case "name":
                if (operator.equalsIgnoreCase("contains")) {
                    return new NameContainsExpression(unquote(value));
                } else {
                    throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid operator for name: " + operator + ". Use contains");
                }

            case "date":
                LocalDate date = parseDate(value);
                if (operator.equalsIgnoreCase("after")) {
                    return new DateAfterExpression(date);
                } else if (operator.equalsIgnoreCase("before")) {
                    return new DateBeforeExpression(date);
                } else {
                    throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid operator for date: " + operator + ". Use after or before");
                }

            default:
                throw new HttpException(HttpStatus.BAD_REQUEST, "Unknown field: " + field);
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid number format: " + value);
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid date format: " + value + ". Use YYYY-MM-DD");
        }
    }

    private String unquote(String value) {
        if (value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
