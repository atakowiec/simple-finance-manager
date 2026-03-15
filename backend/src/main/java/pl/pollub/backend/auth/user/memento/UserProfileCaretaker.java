package pl.pollub.backend.auth.user.memento;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Stores per-user profile history for undo operations.
 */
@Component
@Slf4j
public class UserProfileCaretaker {
    private static final int MAX_HISTORY_SIZE = 10;
    private final Map<Long, Stack<UserProfileMemento>> userHistories = new HashMap<>();

    public void saveMemento(Long userId, UserProfileMemento memento) {
        Stack<UserProfileMemento> history = userHistories.computeIfAbsent(userId, key -> new Stack<>());

        if (history.size() >= MAX_HISTORY_SIZE) {
            history.remove(0);
        }

        history.push(memento);
        log.debug("Saved user profile memento for userId={}", userId);
    }

    public UserProfileMemento getLastMemento(Long userId) {
        Stack<UserProfileMemento> history = userHistories.get(userId);
        if (history == null || history.isEmpty()) {
            return null;
        }

        UserProfileMemento memento = history.pop();
        log.debug("Restored user profile memento for userId={}", userId);
        return memento;
    }

    public boolean hasHistory(Long userId) {
        Stack<UserProfileMemento> history = userHistories.get(userId);
        return history != null && !history.isEmpty();
    }

    public void clearHistory(Long userId) {
        userHistories.remove(userId);
        log.debug("Cleared user profile history for userId={}", userId);
    }
}

