package pl.pollub.backend.group.export;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import pl.pollub.backend.group.dto.ImportExportDto;

/**
 * Command that exports group data as JSON.
 */
@Component
public class JsonExportCommand implements ExportCommand {
    @Override
    public String getFormat() {
        return "json";
    }

    @Override
    public GroupExportResponse execute(ImportExportDto data) {
        DataExporter exporter = new JsonFormatterDecorator(new BaseDataExporter());
        return new GroupExportResponse(exporter.export(data), MediaType.APPLICATION_JSON_VALUE, "export.json");
    }
}

