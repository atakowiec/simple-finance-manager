package pl.pollub.backend.group.export;

import pl.pollub.backend.group.dto.ImportExportDto;

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

