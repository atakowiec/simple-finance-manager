package pl.pollub.backend.group.export;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ExportCommandFactoryTest {

    @Test
    void getCommand_SupportedFormats_ReturnsMatchingCommand() {
        ExportCommandFactory factory = new ExportCommandFactory(List.of(new JsonExportCommand(), new CsvExportCommand()));

        ExportCommand json = factory.getCommand("json");
        ExportCommand csv = factory.getCommand("CSV");

        Assertions.assertNotNull(json);
        Assertions.assertNotNull(csv);
        Assertions.assertEquals("json", json.getFormat());
        Assertions.assertEquals("csv", csv.getFormat());
    }

    @Test
    void getCommand_UnsupportedFormat_ReturnsNull() {
        ExportCommandFactory factory = new ExportCommandFactory(List.of(new JsonExportCommand(), new CsvExportCommand()));

        ExportCommand command = factory.getCommand("xml");

        Assertions.assertNull(command);
    }
}

