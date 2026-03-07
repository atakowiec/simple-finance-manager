package pl.pollub.backend.transaction.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.model.CategoryType;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.model.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Transaction Query Language using Interpreter pattern.
 */
class TransactionQueryInterpreterTest {

    private TransactionQueryParser parser;
    private TransactionQueryInterpreter interpreter;
    private List<Transaction> testTransactions;

    @BeforeEach
    void setUp() {
        parser = new TransactionQueryParser();
        interpreter = new TransactionQueryInterpreter(parser);

        // Create test data
        testTransactions = new ArrayList<>();
        User user = new User();
        Group group = new Group();

        TransactionCategory foodCategory = new TransactionCategory(
                new TransactionCategory.BasicData(1L, "Food", CategoryType.EXPENSE),
                null
        );
        TransactionCategory transportCategory = new TransactionCategory(
                new TransactionCategory.BasicData(2L, "Transport", CategoryType.EXPENSE),
                null
        );

        Expense expense1 = new Expense();
        expense1.setId(1L);
        expense1.setName("Coffee at Starbucks");
        expense1.setAmount(50.0);
        expense1.setCategory(foodCategory);
        expense1.setDate(LocalDate.of(2026, 3, 1));
        expense1.setUser(user);
        expense1.setGroup(group);

        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setName("Lunch");
        expense2.setAmount(150.0);
        expense2.setCategory(foodCategory);
        expense2.setDate(LocalDate.of(2026, 3, 5));
        expense2.setUser(user);
        expense2.setGroup(group);

        Expense expense3 = new Expense();
        expense3.setId(3L);
        expense3.setName("Bus ticket");
        expense3.setAmount(30.0);
        expense3.setCategory(transportCategory);
        expense3.setDate(LocalDate.of(2026, 2, 28));
        expense3.setUser(user);
        expense3.setGroup(group);

        Expense expense4 = new Expense();
        expense4.setId(4L);
        expense4.setName("Tea");
        expense4.setAmount(20.0);
        expense4.setCategory(foodCategory);
        expense4.setDate(LocalDate.of(2026, 3, 6));
        expense4.setUser(user);
        expense4.setGroup(group);

        testTransactions.add(expense1);
        testTransactions.add(expense2);
        testTransactions.add(expense3);
        testTransactions.add(expense4);
    }

    @Test
    void testAmountGreaterThan() {
        String query = "amount > 100";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getName());
    }

    @Test
    void testAmountLessThan() {
        String query = "amount < 40";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(2, result.size());
    }

    @Test
    void testCategoryEquals() {
        String query = "category = 'Food'";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(3, result.size());
    }

    @Test
    void testNameContains() {
        String query = "name contains 'coffee'";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(1, result.size());
        assertEquals("Coffee at Starbucks", result.get(0).getName());
    }

    @Test
    void testDateAfter() {
        String query = "date after 2026-03-01";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(2, result.size());
    }

    @Test
    void testDateBefore() {
        String query = "date before 2026-03-01";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(1, result.size());
        assertEquals("Bus ticket", result.get(0).getName());
    }

    @Test
    void testAndExpression() {
        String query = "amount > 100 AND category = 'Food'";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getName());
    }

    @Test
    void testOrExpression() {
        String query = "name contains 'coffee' OR name contains 'tea'";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(2, result.size());
    }

    @Test
    void testNotExpression() {
        String query = "NOT category = 'Food'";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(1, result.size());
        assertEquals("Bus ticket", result.get(0).getName());
    }

    @Test
    void testComplexQuery() {
        String query = "amount < 100 AND category = 'Food' AND date after 2026-03-01";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(1, result.size());
        assertEquals("Tea", result.get(0).getName());
    }

    @Test
    void testComplexQueryWithOr() {
        String query = "category = 'Food' OR category = 'Transport'";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(4, result.size());
    }

    @Test
    void testComplexQueryWithNotAndOr() {
        String query = "NOT category = 'Transport' AND amount < 100";
        List<Transaction> result = interpreter.filter(testTransactions, query);

        assertEquals(2, result.size());
    }

    @Test
    void testMatches() {
        Transaction transaction = testTransactions.get(0); // Coffee at Starbucks
        assertTrue(interpreter.matches(transaction, "name contains 'coffee'"));
        assertFalse(interpreter.matches(transaction, "amount > 100"));
    }
}

