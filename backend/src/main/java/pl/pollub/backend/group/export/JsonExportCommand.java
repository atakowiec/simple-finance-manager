package pl.pollub.backend.group.export;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Command that exports group data as JSON.
 */
@Component
public class JsonExportCommand extends AbstractExportCommand {
    @Override
    public String getFormat() {
        return "json";
    }

    @Override
    protected DataExporter createExporter() {
        return new JsonFormatterDecorator(new BaseDataExporter());
    }

    @Override
    protected String getContentType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }

    @Override
    protected String getFilename() {
        return "export.json";
    }
}
