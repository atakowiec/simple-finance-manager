package pl.pollub.backend.group.export;

import pl.pollub.backend.group.dto.ImportExportDto;

// start decorator
/**
 * Component interface for the Decorator design pattern.
 */
public interface DataExporter {
    byte[] export(ImportExportDto data);
}
