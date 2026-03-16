package pl.pollub.backend.group.memento;

import lombok.AllArgsConstructor;
import lombok.Data;

// start memento
/**
 * Memento class that stores the state of a Group.
 * Part of the Memento design pattern implementation for group edit undo functionality.
 */
@Data
@AllArgsConstructor
public class GroupMemento {
    private final String name;
    private final String color;
    private final GroupIconFlyweight iconFlyweight;
    private final double expenseLimit;
    private final String expenseLimitRule;
    private boolean released;

    /**
     * Creates a memento from the current state.
     */
    public static GroupMemento create(
            String name,
            String color,
            byte[] icon,
            String iconContentType,
            double expenseLimit,
            String expenseLimitRule
    ) {
        return new GroupMemento(
                name,
                color,
                GroupIconFlyweightFactory.acquire(icon, iconContentType),
                expenseLimit,
                expenseLimitRule,
                false
        );
    }

    public byte[] getIcon() {
        return iconFlyweight == null ? null : iconFlyweight.getIcon();
    }

    public String getIconContentType() {
        return iconFlyweight == null ? null : iconFlyweight.getContentType();
    }

    public synchronized void release() {
        if (released) {
            return;
        }

        GroupIconFlyweightFactory.release(iconFlyweight);
        released = true;
    }
}

