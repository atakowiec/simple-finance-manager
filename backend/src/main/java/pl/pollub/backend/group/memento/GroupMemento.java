package pl.pollub.backend.group.memento;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Memento class that stores the state of a Group.
 * Part of the Memento design pattern implementation for group edit undo functionality.
 */
@Data
@AllArgsConstructor
public class GroupMemento {
    private final String name;
    private final String color;
    private final double expenseLimit;

    /**
     * Creates a memento from the current state.
     */
    public static GroupMemento create(String name, String color, double expenseLimit) {
        return new GroupMemento(name, color, expenseLimit);
    }
}

