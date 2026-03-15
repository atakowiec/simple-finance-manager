package pl.pollub.backend.categories.memento;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.pollub.backend.categories.model.CategoryType;

import java.util.Arrays;

/**
 * Snapshot of mutable category fields used by undo operation.
 */
@Data
@AllArgsConstructor
public class CategoryMemento {
    private final String name;
    private final CategoryType categoryType;
    private final byte[] icon;
    private final Long parentId;

    public static CategoryMemento create(String name, CategoryType categoryType, byte[] icon, Long parentId) {
        byte[] iconCopy = icon == null ? null : Arrays.copyOf(icon, icon.length);
        return new CategoryMemento(name, categoryType, iconCopy, parentId);
    }

    public byte[] getIcon() {
        return icon == null ? null : Arrays.copyOf(icon, icon.length);
    }
}


