package pl.pollub.backend.group.export;

import org.springframework.stereotype.Component;

/**
 * Command that exports group data as CSV.
 */
@Component
public class CsvExportCommand extends AbstractExportCommand {
    @Override
    public String getFormat() {
        return "csv";
    }

    @Override
    protected DataExporter createExporter() {
        return new CsvFormatterDecorator(new BaseDataExporter());
    }

    @Override
    protected String getContentType() {
        return "text/csv";
    }

    @Override
    protected String getFilename() {
        return "export.csv";
    }
}
