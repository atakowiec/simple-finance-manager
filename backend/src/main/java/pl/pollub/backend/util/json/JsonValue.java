package pl.pollub.backend.util.json;

/**
 * Leaf JSON node for primitive values.
 */
public class JsonValue implements JsonComponent {
    private final Object value;

    public JsonValue(Object value) {
        this.value = value;
    }

    @Override
    public Object toRaw() {
        return value;
    }
}
