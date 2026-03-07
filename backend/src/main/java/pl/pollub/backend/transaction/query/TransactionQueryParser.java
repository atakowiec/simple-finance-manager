package pl.pollub.backend.transaction.query;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.HttpException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

// start single responsibility principle
/**
 * Parser for Transaction Query Language.
 * Parses query strings into Expression objects using the Interpreter pattern.
 * Supported syntax:
 * - amount > 100
 * - amount < 50
 * - category = 'Food'
 * - name contains 'coffee'
 * - date after 2026-01-01
 * - date before 2026-12-31
 * - Logical operators: AND, OR, NOT
 * Examples:
 * - "amount > 100 AND category = 'Food'"
 * - "name contains 'coffee' OR name contains 'tea'"
 * - "date after 2026-01-01 AND amount < 50"
 */
@Component
public class TransactionQueryParser {

    public Expression parse(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Query cannot be empty");
        }

        // Tokenize the query
        List<String> tokens = tokenize(query.trim());

        // Parse the tokens into an expression tree
        return parseOrExpression(tokens, 0).expression;
    }

    private List<String> tokenize(String query) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);

            if (c == '\'') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == ' ' && !inQuotes) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    private ParseResult parseOrExpression(List<String> tokens, int start) {
        ParseResult left = parseAndExpression(tokens, start);
        int pos = left.nextPosition;

        while (pos < tokens.size() && tokens.get(pos).equalsIgnoreCase("OR")) {
            ParseResult right = parseAndExpression(tokens, pos + 1);
            left = new ParseResult(new OrExpression(left.expression, right.expression), right.nextPosition);
            pos = left.nextPosition;
        }

        return left;
    }

    private ParseResult parseAndExpression(List<String> tokens, int start) {
        ParseResult left = parseNotExpression(tokens, start);
        int pos = left.nextPosition;

        while (pos < tokens.size() && tokens.get(pos).equalsIgnoreCase("AND")) {
            ParseResult right = parseNotExpression(tokens, pos + 1);
            left = new ParseResult(new AndExpression(left.expression, right.expression), right.nextPosition);
            pos = left.nextPosition;
        }

        return left;
    }

    private ParseResult parseNotExpression(List<String> tokens, int start) {
        if (start >= tokens.size()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Unexpected end of query");
        }

        if (tokens.get(start).equalsIgnoreCase("NOT")) {
            ParseResult result = parsePrimaryExpression(tokens, start + 1);
            return new ParseResult(new NotExpression(result.expression), result.nextPosition);
        }

        return parsePrimaryExpression(tokens, start);
    }

    private ParseResult parsePrimaryExpression(List<String> tokens, int start) {
        if (start >= tokens.size()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Unexpected end of query");
        }

        String field = tokens.get(start);

        if (start + 2 >= tokens.size()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Incomplete expression starting with: " + field);
        }

        String operator = tokens.get(start + 1);
        String value = tokens.get(start + 2);

        Expression expression = createExpression(field, operator, value);
        return new ParseResult(expression, start + 3);
    }

    private Expression createExpression(String field, String operator, String value) {
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

    private static class ParseResult {
        final Expression expression;
        final int nextPosition;

        ParseResult(Expression expression, int nextPosition) {
            this.expression = expression;
            this.nextPosition = nextPosition;
        }
    }
}

