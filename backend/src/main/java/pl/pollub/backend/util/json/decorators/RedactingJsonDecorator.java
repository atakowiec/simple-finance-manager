package pl.pollub.backend.util.json.decorators;

import pl.pollub.backend.util.json.JsonComponent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Decorator that redacts selected JSON fields by key.
 */
public class RedactingJsonDecorator implements JsonComponent {
    private static final String REDACTED = "***REDACTED***";

    private final JsonComponent delegate;
    private final Set<String> keysToRedact;

    public RedactingJsonDecorator(JsonComponent delegate, Set<String> keysToRedact) {
        this.delegate = delegate;
        this.keysToRedact = keysToRedact;
    }

    @Override
    public Object toRaw() {
        Object raw = delegate == null ? null : delegate.toRaw();
        return redact(raw);
    }

    private Object redact(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> redacted = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                Object entryValue = entry.getValue();
                if (keysToRedact.contains(key)) {
                    redacted.put(key, REDACTED);
                } else {
                    redacted.put(key, redact(entryValue));
                }
            }
            return redacted;
        }
        if (value instanceof Iterable<?> iterable) {
            List<Object> redacted = new ArrayList<>();
            for (Object item : iterable) {
                redacted.add(redact(item));
            }
            return redacted;
        }
        return value;
    }
}
