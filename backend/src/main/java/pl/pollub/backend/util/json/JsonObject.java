package pl.pollub.backend.util.json;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Composite JSON object node.
 */
public class JsonObject implements JsonComponent {
    private final Map<String, JsonComponent> fields = new LinkedHashMap<>();

    public JsonObject add(String key, JsonComponent value) {
        Objects.requireNonNull(key, "key");
        fields.put(key, value);
        return this;
    }

    public Map<String, JsonComponent> getChildren() {
        return Collections.unmodifiableMap(fields);
    }

    @Override
    public Object toRaw() {
        Map<String, Object> raw = new LinkedHashMap<>();
        for (Map.Entry<String, JsonComponent> entry : fields.entrySet()) {
            raw.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toRaw());
        }
        return raw;
    }
}
