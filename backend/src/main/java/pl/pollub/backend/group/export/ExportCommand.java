package pl.pollub.backend.group.export;

import pl.pollub.backend.group.dto.ImportExportDto;

/**
 * Command interface for exporting data in a specific format.
 */
public interface ExportCommand {
    String getFormat();

    GroupExportResponse execute(ImportExportDto data);
}

