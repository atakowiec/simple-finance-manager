package pl.pollub.backend.transaction.amount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pollub.backend.exception.HttpException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AmountExpressionInterpreterTest {

    private AmountExpressionInterpreter interpreter;

    @BeforeEach
    void setUp() {
        interpreter = new AmountExpressionInterpreter();
    }

    @Test
    void shouldInterpretAdditionAndSubtraction() {
        double result = interpreter.interpret("100+100-20");

        assertEquals(180.0, result);
    }

    @Test
    void shouldInterpretWhitespacesAndCommaSeparator() {
        double result = interpreter.interpret(" 10,5 + 9,5 - 5 ");

        assertEquals(15.0, result);
    }

    @Test
    void shouldAllowLeadingSign() {
        double result = interpreter.interpret("-10+20");

        assertEquals(10.0, result);
    }

    @Test
    void shouldRejectInvalidCharacters() {
        assertThrows(HttpException.class, () -> interpreter.interpret("10*2"));
    }

    @Test
    void shouldRejectTrailingOperator() {
        assertThrows(HttpException.class, () -> interpreter.interpret("10+"));
    }
}

