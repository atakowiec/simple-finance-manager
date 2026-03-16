package pl.pollub.backend.util.json.decorators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.pollub.backend.util.json.JsonComponent;

// start decorator
/**
 * Decorator that provides pretty-printed JSON output.
 */
public class PrettyPrintJsonDecorator implements JsonComponent {
    private final JsonComponent delegate;
    private final ObjectMapper objectMapper;

    public PrettyPrintJsonDecorator(JsonComponent delegate) {
        this(delegate, new ObjectMapper());
    }

    public PrettyPrintJsonDecorator(JsonComponent delegate, ObjectMapper objectMapper) {
        this.delegate = delegate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object toRaw() {
        return delegate == null ? null : delegate.toRaw();
    }

    public String toPrettyJson() {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(toRaw());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
