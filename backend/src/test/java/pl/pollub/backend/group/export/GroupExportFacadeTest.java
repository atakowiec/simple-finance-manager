package pl.pollub.backend.group.export;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.interfaces.GroupService;

@ExtendWith(MockitoExtension.class)
class GroupExportFacadeTest {

    @Mock
    private GroupService groupService;

    @Mock
    private ExportCommandFactory exportCommandFactory;

    @Mock
    private ExportCommand exportCommand;

    @InjectMocks
    private GroupExportFacade groupExportFacade;

    @Test
    void exportGroupData_DelegatesToResolvedCommand() {
        User user = new User();
        user.setId(1L);

        ImportExportDto exportData = new ImportExportDto();
        GroupExportResponse expectedResponse = new GroupExportResponse(new byte[]{1, 2, 3}, "text/csv", "export.csv");

        Mockito.when(groupService.exportTransactions(user, 10L)).thenReturn(exportData);
        Mockito.when(exportCommandFactory.getCommand("csv")).thenReturn(exportCommand);
        Mockito.when(exportCommand.execute(exportData)).thenReturn(expectedResponse);

        GroupExportResponse response = groupExportFacade.exportGroupData(user, 10L, "csv");

        Assertions.assertSame(expectedResponse, response);
        Mockito.verify(groupService, Mockito.times(1)).exportTransactions(user, 10L);
        Mockito.verify(exportCommandFactory, Mockito.times(1)).getCommand("csv");
        Mockito.verify(exportCommand, Mockito.times(1)).execute(exportData);
    }
}

