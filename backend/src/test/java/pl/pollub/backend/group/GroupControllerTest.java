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
import pl.pollub.backend.group.dto.GroupMemberDto;
import pl.pollub.backend.group.export.GroupExportFacade;
import pl.pollub.backend.group.export.GroupExportResponse;
import pl.pollub.backend.group.interfaces.GroupInviteService;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;

import java.util.List;

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
    void getGroupOwner_DelegatesToService() {
        User user = new User();
        user.setId(1L);

        List<GroupMemberDto> expectedOwners = List.of(new GroupMemberDto(10L, "owner", true));
        Mockito.when(groupService.getGroupOwners(user, 7L)).thenReturn(expectedOwners);

        List<GroupMemberDto> response = groupController.getGroupOwners(user, 7L);

        Assertions.assertEquals(expectedOwners, response);
        Mockito.verify(groupService).getGroupOwners(user, 7L);
    }

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

    @Test
    void getGroupIcon_GroupHasIcon_ReturnsImageBytes() {
        User user = new User();
        user.setId(1L);

        Group group = new Group();
        group.setId(3L);
        group.setUsers(List.of(user));
        group.setIcon(new byte[]{1, 2, 3});
        group.setIconContentType("image/png");

        Mockito.when(groupService.getGroupByIdOrThrow(3L)).thenReturn(group);

        ResponseEntity<byte[]> response = groupController.getGroupIcon(user, 3L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertArrayEquals(group.getIcon(), response.getBody());
        Assertions.assertEquals("image/png", response.getHeaders().getContentType().toString());
        Mockito.verify(groupService).checkMembershipOrThrow(user, group);
    }

    @Test
    void getGroupIcon_GroupHasNoIcon_ThrowsHttp404Exception() {
        User user = new User();
        user.setId(1L);

        Group group = new Group();
        group.setId(4L);
        group.setUsers(List.of(user));

        Mockito.when(groupService.getGroupByIdOrThrow(4L)).thenReturn(group);

        HttpException exception = Assertions.assertThrows(HttpException.class,
                () -> groupController.getGroupIcon(user, 4L));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(groupService).checkMembershipOrThrow(user, group);
    }
}

