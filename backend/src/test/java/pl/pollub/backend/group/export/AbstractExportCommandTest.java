package pl.pollub.backend.group.export;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.pollub.backend.group.dto.ImportExportDto;

import java.nio.charset.StandardCharsets;

class AbstractExportCommandTest {

    @Test
    void execute_UsesTemplateHooksToBuildResponse() {
        ImportExportDto data = new ImportExportDto();
        byte[] payload = "template".getBytes(StandardCharsets.UTF_8);

        ExportCommand command = new StubExportCommand(payload, "application/test", "test.bin");

        GroupExportResponse response = command.execute(data);

        Assertions.assertArrayEquals(payload, response.getData());
        Assertions.assertEquals("application/test", response.getContentType());
        Assertions.assertEquals("test.bin", response.getFilename());
    }

    private static class StubExportCommand extends AbstractExportCommand {
        private final byte[] payload;
        private final String contentType;
        private final String filename;

        private StubExportCommand(byte[] payload, String contentType, String filename) {
            this.payload = payload;
            this.contentType = contentType;
            this.filename = filename;
        }

        @Override
        public String getFormat() {
            return "stub";
        }

        @Override
        protected DataExporter createExporter() {
            return data -> payload;
        }

        @Override
        protected String getContentType() {
            return contentType;
        }

        @Override
        protected String getFilename() {
            return filename;
        }
    }
}

