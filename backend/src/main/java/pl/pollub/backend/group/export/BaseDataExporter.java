package pl.pollub.backend.group.export;

import pl.pollub.backend.group.dto.ImportExportDto;

import java.nio.charset.StandardCharsets;

/**
 * Concrete Component for the Decorator design pattern.
 */
public class BaseDataExporter implements DataExporter {
    @Override
    public byte[] export(ImportExportDto data) {
        // Return raw data string as bytes
        return data.toString().getBytes(StandardCharsets.UTF_8);
    }
}
