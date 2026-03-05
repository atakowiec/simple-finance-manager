package pl.pollub.backend.group.export;

import lombok.RequiredArgsConstructor;
import pl.pollub.backend.group.dto.ImportExportDto;

/**
 * Base Decorator for the Decorator design pattern.
 */
@RequiredArgsConstructor
public abstract class DataFormatterDecorator implements DataExporter {
    protected final DataExporter wrappedExporter;

    @Override
    public byte[] export(ImportExportDto data) {
        return wrappedExporter.export(data);
    }
}
