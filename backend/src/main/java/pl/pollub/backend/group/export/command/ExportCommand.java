package pl.pollub.backend.group.export.command;

import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.export.GroupExportResponse;

// start interface segregation principle
/**
 * Command interface for exporting data in a specific format.
 */
public interface ExportCommand {
    String getFormat();

    GroupExportResponse execute(ImportExportDto data);
}

