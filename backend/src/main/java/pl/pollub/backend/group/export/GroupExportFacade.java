package pl.pollub.backend.group.export;

// start facade
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.interfaces.GroupService;

/**
 * Facade for group data export functionality.
 * Simplifies the process of exporting group data by orchestrating GroupService
 * and various DataExporter decorators.
 */
@Component
@RequiredArgsConstructor
public class GroupExportFacade {
    private final GroupService groupService;

    public GroupExportResponse exportGroupData(User user, Long groupId, String format) {
        ImportExportDto exportDto = groupService.exportTransactions(user, groupId);
        
        DataExporter exporter = new BaseDataExporter();
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String filename = "export";

        if ("csv".equalsIgnoreCase(format)) {
            exporter = new CsvFormatterDecorator(exporter);
            contentType = "text/csv";
            filename += ".csv";
        } else if ("json".equalsIgnoreCase(format)) {
            exporter = new JsonFormatterDecorator(exporter);
            contentType = MediaType.APPLICATION_JSON_VALUE;
            filename += ".json";
        }

        byte[] data = exporter.export(exportDto);
        return new GroupExportResponse(data, contentType, filename);
    }
}
