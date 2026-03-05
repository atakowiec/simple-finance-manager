package pl.pollub.backend.group.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.pollub.backend.group.dto.ImportExportDto;

/**
 * Concrete Decorator for JSON formatting.
 */
public class JsonFormatterDecorator extends DataFormatterDecorator {
    private final ObjectMapper objectMapper;

    public JsonFormatterDecorator(DataExporter exporter) {
        super(exporter);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public byte[] export(ImportExportDto data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to format data as JSON", e);
        }
    }
}
