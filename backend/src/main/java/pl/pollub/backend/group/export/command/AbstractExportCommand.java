package pl.pollub.backend.group.export.command;

import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.export.DataExporter;
import pl.pollub.backend.group.export.GroupExportResponse;

// start liskov substitution principle
// start dependency inversion principle
// start template
/**
 * Template Method base for export commands.
 * Keeps command selection/execution intact while sharing export workflow.
 */
public abstract class AbstractExportCommand implements ExportCommand {

    @Override
    public final GroupExportResponse execute(ImportExportDto data) {
        DataExporter exporter = createExporter();
        return new GroupExportResponse(exporter.export(data), getContentType(), getFilename());
    }

    protected abstract DataExporter createExporter();

    protected abstract String getContentType();

    protected abstract String getFilename();
}

