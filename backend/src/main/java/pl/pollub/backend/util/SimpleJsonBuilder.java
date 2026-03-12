package pl.pollub.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.pollub.backend.util.json.JsonArray;
import pl.pollub.backend.util.json.JsonComponent;
import pl.pollub.backend.util.json.JsonObject;
import pl.pollub.backend.util.json.JsonValue;
import pl.pollub.backend.util.json.decorators.PrettyPrintJsonDecorator;
import pl.pollub.backend.util.json.decorators.RedactingJsonDecorator;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

// start builder

/**
 * Simple JSON builder that allows to create JSON objects in a more readable way.
 */
public class SimpleJsonBuilder {
    private final JsonObject root;

    private SimpleJsonBuilder() {
        this.root = new JsonObject();
    }

    private SimpleJsonBuilder(Map<String, ?> map) {
        this.root = new JsonObject();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            root.add(entry.getKey(), toComponent(entry.getValue()));
        }
    }

    /**
     * Creates a new JSON object with a single key-value pair.
     *
     * @param key   key of the JSON object
     * @param value value of the JSON object
     * @return JSON builder with the specified key-value pair
     */
    public static SimpleJsonBuilder of(String key, Object value) {
        return new SimpleJsonBuilder().add(key, value);
    }

    /**
     * Creates a new JSON object with the specified key-value pairs.
     *
     * @param map key-value pairs of the JSON object
     * @return JSON builder with the specified key-value pairs
     */
    public static SimpleJsonBuilder of(Map<String, ?> map) {
        return new SimpleJsonBuilder(map);
    }

    /**
     * Creates an empty JSON object.
     *
     * @return empty JSON builder
     */
    public static SimpleJsonBuilder empty() {
        return new SimpleJsonBuilder();
    }

    /**
     * Adds a new key-value pair to the JSON object.
     *
     * @param key   key of the JSON object
     * @param value value of the JSON object
     * @return JSON builder with the new key-value pair
     */
    public SimpleJsonBuilder add(String key, Object value) {
        root.add(key, toComponent(value));
        return this;
    }

    /**
     * Returns Map representation of the JSON object.
     *
     * @return JSON object as a Map
     */
    public Map<String, Object> build() {
        Object raw = root.toRaw();
        if (raw instanceof Map<?, ?> rawMap) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                result.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            return result;
        }
        throw new IllegalStateException("Root JSON component is not an object");
    }

    /**
     * Converts the JSON object to a JSON string.
     *
     * @return JSON string
     */
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(root.toRaw());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the JSON object to a JSON string with selected fields redacted.
     *
     * @param keysToRedact keys to be redacted
     * @return redacted JSON string
     */
    public String toRedactedJson(Set<String> keysToRedact) {
        JsonComponent redacted = new RedactingJsonDecorator(root, keysToRedact);
        return new PrettyPrintJsonDecorator(redacted).toPrettyJson();
    }

    JsonComponent toComponent() {
        return root;
    }

    private static JsonComponent toComponent(Object value) {
        if (value == null) {
            return new JsonValue(null);
        }
        if (value instanceof JsonComponent component) {
            return component;
        }
        if (value instanceof SimpleJsonBuilder builder) {
            return builder.toComponent();
        }
        if (value instanceof Map<?, ?> map) {
            JsonObject object = new JsonObject();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                object.add(String.valueOf(entry.getKey()), toComponent(entry.getValue()));
            }
            return object;
        }
        if (value instanceof Iterable<?> iterable) {
            JsonArray array = new JsonArray();
            for (Object item : iterable) {
                array.add(toComponent(item));
            }
            return array;
        }
        Class<?> valueClass = value.getClass();
        if (valueClass.isArray()) {
            JsonArray array = new JsonArray();
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                array.add(toComponent(Array.get(value, i)));
            }
            return array;
        }
        return new JsonValue(value);
    }
}
