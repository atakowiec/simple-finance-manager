package pl.pollub.backend.util.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Composite JSON array node.
 */
public class JsonArray implements JsonComponent {
    private final List<JsonComponent> items = new ArrayList<>();

    public JsonArray add(JsonComponent item) {
        Objects.requireNonNull(item, "item");
        items.add(item);
        return this;
    }

    public List<JsonComponent> getChildren() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public Object toRaw() {
        List<Object> raw = new ArrayList<>(items.size());
        for (JsonComponent item : items) {
            raw.add(item == null ? null : item.toRaw());
        }
        return raw;
    }
}
