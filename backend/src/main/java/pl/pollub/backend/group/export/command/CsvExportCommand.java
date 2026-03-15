package pl.pollub.backend.group.export.command;

import org.springframework.stereotype.Component;
import pl.pollub.backend.group.export.BaseDataExporter;
import pl.pollub.backend.group.export.decorator.CsvFormatterDecorator;
import pl.pollub.backend.group.export.DataExporter;

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
