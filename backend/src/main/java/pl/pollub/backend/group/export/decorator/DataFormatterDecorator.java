package pl.pollub.backend.group.export.decorator;

import lombok.RequiredArgsConstructor;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.export.DataExporter;

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
