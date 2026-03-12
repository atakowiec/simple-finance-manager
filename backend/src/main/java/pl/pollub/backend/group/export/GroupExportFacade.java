package pl.pollub.backend.group.export;

// start facade
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.interfaces.GroupService;

/**
 * Facade for group data export functionality.
 * Simplifies the process of exporting group data by orchestrating GroupService
 * and export commands.
 */
@Component
public class GroupExportFacade {
    private final GroupService groupService;
    private final ExportCommandFactory exportCommandFactory;

    public GroupExportFacade(GroupService groupService, java.util.List<ExportCommand> commands) {
        this.groupService = groupService;
        ExportCommandFactory.init(commands);
        this.exportCommandFactory = ExportCommandFactory.getInstance();
    }

    public GroupExportResponse exportGroupData(User user, Long groupId, String format) {
        ImportExportDto exportDto = groupService.exportTransactions(user, groupId);
        // start command
        ExportCommand command = exportCommandFactory.getCommand(format);
        return command.execute(exportDto);
    }
}
