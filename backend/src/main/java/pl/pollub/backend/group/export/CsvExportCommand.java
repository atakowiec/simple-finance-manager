package pl.pollub.backend.group.export;

import org.springframework.stereotype.Component;
import pl.pollub.backend.group.dto.ImportExportDto;

/**
 * Command that exports group data as CSV.
 */
@Component
public class CsvExportCommand implements ExportCommand {
    @Override
    public String getFormat() {
        return "csv";
    }

    @Override
    public GroupExportResponse execute(ImportExportDto data) {
        DataExporter exporter = new CsvFormatterDecorator(new BaseDataExporter());
        return new GroupExportResponse(exporter.export(data), "text/csv", "export.csv");
    }
}

