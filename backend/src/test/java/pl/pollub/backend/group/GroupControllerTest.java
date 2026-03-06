package pl.pollub.backend.group;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.export.GroupExportFacade;
import pl.pollub.backend.group.export.GroupExportResponse;
import pl.pollub.backend.group.interfaces.GroupInviteService;
import pl.pollub.backend.group.interfaces.GroupService;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @Mock
    private GroupInviteService groupInviteService;

    @Mock
    private GroupExportFacade groupExportFacade;

    @InjectMocks
    private GroupController groupController;

    @Test
    void handleExport_UnsupportedFormat_ThrowsHttp400Exception() {
        User user = new User();
        user.setId(1L);

        HttpException ex = Assertions.assertThrows(HttpException.class,
                () -> groupController.handleExport(user, 1L, "xml"));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        Mockito.verifyNoInteractions(groupExportFacade);
    }

    @Test
    void handleExport_SupportedFormat_DelegatesToFacade() {
        User user = new User();
        user.setId(1L);

        byte[] payload = new byte[]{10, 20};
        GroupExportResponse facadeResponse = new GroupExportResponse(payload, "text/csv", "export.csv");
        Mockito.when(groupExportFacade.exportGroupData(user, 2L, "csv")).thenReturn(facadeResponse);

        ResponseEntity<byte[]> response = groupController.handleExport(user, 2L, "csv");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertArrayEquals(payload, response.getBody());
        Assertions.assertEquals("attachment; filename=export.csv", response.getHeaders().getFirst("Content-Disposition"));
    }
}

