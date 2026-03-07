package pl.pollub.backend.group.memento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GroupMemento and GroupCaretaker.
 * Validates the Memento design pattern implementation for group edit undo functionality.
 */
class GroupMementoTest {

    private GroupCaretaker caretaker;

    @BeforeEach
    void setUp() {
        caretaker = new GroupCaretaker();
    }

    @Test
    void testMementoCreation() {
        GroupMemento memento = GroupMemento.create("Test Group", "#FF0000", 1000.0);

        assertNotNull(memento);
        assertEquals("Test Group", memento.getName());
        assertEquals("#FF0000", memento.getColor());
        assertEquals(1000.0, memento.getExpenseLimit());
    }

    @Test
    void testSaveAndRetrieveMemento() {
        Long groupId = 1L;
        GroupMemento memento = GroupMemento.create("Original Name", "#00FF00", 500.0);

        caretaker.saveMemento(groupId, memento);

        assertTrue(caretaker.hasHistory(groupId));

        GroupMemento retrieved = caretaker.getLastMemento(groupId);

        assertNotNull(retrieved);
        assertEquals("Original Name", retrieved.getName());
        assertEquals("#00FF00", retrieved.getColor());
        assertEquals(500.0, retrieved.getExpenseLimit());
    }

    @Test
    void testMultipleMementos() {
        Long groupId = 1L;

        GroupMemento memento1 = GroupMemento.create("Name 1", "#FF0000", 100.0);
        GroupMemento memento2 = GroupMemento.create("Name 2", "#00FF00", 200.0);
        GroupMemento memento3 = GroupMemento.create("Name 3", "#0000FF", 300.0);

        caretaker.saveMemento(groupId, memento1);
        caretaker.saveMemento(groupId, memento2);
        caretaker.saveMemento(groupId, memento3);

        assertTrue(caretaker.hasHistory(groupId));

        // Should retrieve in LIFO order (stack)
        GroupMemento retrieved3 = caretaker.getLastMemento(groupId);
        assertEquals("Name 3", retrieved3.getName());

        GroupMemento retrieved2 = caretaker.getLastMemento(groupId);
        assertEquals("Name 2", retrieved2.getName());

        GroupMemento retrieved1 = caretaker.getLastMemento(groupId);
        assertEquals("Name 1", retrieved1.getName());

        assertFalse(caretaker.hasHistory(groupId));
    }

    @Test
    void testNoHistoryAvailable() {
        Long groupId = 1L;

        assertFalse(caretaker.hasHistory(groupId));
        assertNull(caretaker.getLastMemento(groupId));
    }

    @Test
    void testClearHistory() {
        Long groupId = 1L;
        GroupMemento memento = GroupMemento.create("Test", "#FFFFFF", 1000.0);

        caretaker.saveMemento(groupId, memento);
        assertTrue(caretaker.hasHistory(groupId));

        caretaker.clearHistory(groupId);
        assertFalse(caretaker.hasHistory(groupId));
    }

    @Test
    void testMaxHistorySize() {
        Long groupId = 1L;

        // Add more than MAX_HISTORY_SIZE (10) mementos
        for (int i = 0; i < 15; i++) {
            GroupMemento memento = GroupMemento.create("Name " + i, "#FFFFFF", i);
            caretaker.saveMemento(groupId, memento);
        }

        // Should still have history
        assertTrue(caretaker.hasHistory(groupId));

        // Retrieve all available mementos (should be max 10)
        int count = 0;
        while (caretaker.hasHistory(groupId)) {
            caretaker.getLastMemento(groupId);
            count++;
        }

        assertEquals(10, count);
    }

    @Test
    void testSeparateHistoriesForDifferentGroups() {
        Long groupId1 = 1L;
        Long groupId2 = 2L;

        GroupMemento memento1 = GroupMemento.create("Group 1", "#FF0000", 100.0);
        GroupMemento memento2 = GroupMemento.create("Group 2", "#00FF00", 200.0);

        caretaker.saveMemento(groupId1, memento1);
        caretaker.saveMemento(groupId2, memento2);

        assertTrue(caretaker.hasHistory(groupId1));
        assertTrue(caretaker.hasHistory(groupId2));

        GroupMemento retrieved1 = caretaker.getLastMemento(groupId1);
        GroupMemento retrieved2 = caretaker.getLastMemento(groupId2);

        assertEquals("Group 1", retrieved1.getName());
        assertEquals("Group 2", retrieved2.getName());
    }
}

