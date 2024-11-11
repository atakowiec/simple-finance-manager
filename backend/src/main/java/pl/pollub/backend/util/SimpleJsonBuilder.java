package pl.pollub.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple JSON builder that allows to create JSON objects in a more readable way.
 */
public class SimpleJsonBuilder {
    private final Map<String, Object> json;

    private SimpleJsonBuilder() {
        this.json = new HashMap<>();
    }

    private SimpleJsonBuilder(Map<String, ?> map) {
        this.json = new HashMap<>(map);
    }

    /**
     * Creates a new JSON object with a single key-value pair.
     * @param key key of the JSON object
     * @param value value of the JSON object
     * @return JSON builder with the specified key-value pair
     */
    public static SimpleJsonBuilder of(String key, Object value) {
        return new SimpleJsonBuilder().add(key, value);
    }

    /**
     * Creates a new JSON object with the specified key-value pairs.
     * @param map key-value pairs of the JSON object
     * @return JSON builder with the specified key-value pairs
     */
    public static SimpleJsonBuilder of(Map<String, ?> map) {
        return new SimpleJsonBuilder(map);
    }

    /**
     * Creates an empty JSON object.
     * @return empty JSON builder
     */
    public static SimpleJsonBuilder empty() {
        return new SimpleJsonBuilder();
    }

    /**
     * Adds a new key-value pair to the JSON object.
     * @param key key of the JSON object
     * @param value value of the JSON object
     * @return JSON builder with the new key-value pair
     */
    public SimpleJsonBuilder add(String key, Object value) {
        json.put(key, value);
        return this;
    }

    /**
     * Returns Map representation of the JSON object.
     * @return JSON object as a Map
     */
    public Map<String, Object> build() {
        return json;
    }

    /**
     * Converts the JSON object to a JSON string.
     * @return JSON string
     */
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
