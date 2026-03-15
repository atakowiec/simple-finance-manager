package pl.pollub.backend.categories.memento;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Stores per-category history for undo operations.
 */
@Component
@Slf4j
public class CategoryCaretaker {
    private static final int MAX_HISTORY_SIZE = 10;
    private final Map<Long, Stack<CategoryMemento>> categoryHistories = new HashMap<>();

    public void saveMemento(Long categoryId, CategoryMemento memento) {
        Stack<CategoryMemento> history = categoryHistories.computeIfAbsent(categoryId, key -> new Stack<>());

        if (history.size() >= MAX_HISTORY_SIZE) {
            history.remove(0);
        }

        history.push(memento);
        log.debug("Saved category memento for categoryId={}", categoryId);
    }

    public CategoryMemento getLastMemento(Long categoryId) {
        Stack<CategoryMemento> history = categoryHistories.get(categoryId);
        if (history == null || history.isEmpty()) {
            return null;
        }

        CategoryMemento memento = history.pop();
        if (history.isEmpty()) {
            categoryHistories.remove(categoryId);
        }
        log.debug("Restored category memento for categoryId={}", categoryId);
        return memento;
    }

    public boolean hasHistory(Long categoryId) {
        Stack<CategoryMemento> history = categoryHistories.get(categoryId);
        return history != null && !history.isEmpty();
    }

    public void clearHistory(Long categoryId) {
        categoryHistories.remove(categoryId);
        log.debug("Cleared category history for categoryId={}", categoryId);
    }
}

