package pl.pollub.backend.group.memento;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

// start memento
/**
 * Caretaker class that manages the history of group states.
 * Part of the Memento design pattern implementation for group edit undo functionality.
 * Maintains a separate undo stack for each group.
 */
@Component
@Slf4j
public class GroupCaretaker {
    private final Map<Long, Stack<GroupMemento>> groupHistories = new HashMap<>();
    private static final int MAX_HISTORY_SIZE = 10;

    /**
     * Saves a memento to the history for the specified group.
     * @param groupId The ID of the group
     * @param memento The memento to save
     */
    public void saveMemento(Long groupId, GroupMemento memento) {
        Stack<GroupMemento> history = groupHistories.computeIfAbsent(groupId, k -> new Stack<>());

        // Limit the history size to prevent memory issues
        if (history.size() >= MAX_HISTORY_SIZE) {
            GroupMemento removed = history.remove(0);
            removed.release();
        }

        history.push(memento);
        log.debug("Saved memento for group {}: {}", groupId, memento);
    }

    /**
     * Retrieves the last memento from the history for the specified group.
     * @param groupId The ID of the group
     * @return The last memento, or null if no history exists
     */
    public GroupMemento getLastMemento(Long groupId) {
        Stack<GroupMemento> history = groupHistories.get(groupId);

        if (history == null || history.isEmpty()) {
            log.debug("No history found for group {}", groupId);
            return null;
        }

        GroupMemento memento = history.pop();
        if (history.isEmpty()) {
            groupHistories.remove(groupId);
        }
        log.debug("Retrieved memento for group {}: {}", groupId, memento);
        return memento;
    }

    /**
     * Checks if there is any history available for the specified group.
     * @param groupId The ID of the group
     * @return true if history exists, false otherwise
     */
    public boolean hasHistory(Long groupId) {
        Stack<GroupMemento> history = groupHistories.get(groupId);
        return history != null && !history.isEmpty();
    }

    /**
     * Clears the history for the specified group.
     * @param groupId The ID of the group
     */
    public void clearHistory(Long groupId) {
        Stack<GroupMemento> history = groupHistories.remove(groupId);
        if (history == null) {
            return;
        }

        history.forEach(GroupMemento::release);
    }
}

