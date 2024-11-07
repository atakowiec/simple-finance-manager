package pl.pollub.frontend.controller.group;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.controller.group.member.GroupMemberListCell;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.GroupMember;
import pl.pollub.frontend.service.ModalService;

import java.util.List;
import java.util.Map;

public class MembersController extends AbstractGroupController {
    @Inject
    private DependencyInjector dependencyInjector;
    @Inject
    private ModalService modalService;

    @FXML
    private ListView<GroupMember> mainList;

    @PostInitialize
    public void postInitialize() {
        onGroupUpdate();

        mainList.setCellFactory(param -> getGroupMemberListCell());
    }

    private GroupMemberListCell getGroupMemberListCell() {
        GroupMemberListCell listCell = new GroupMemberListCell(getGroup());
        dependencyInjector.manualInject(listCell);
        dependencyInjector.runPostInitialize(listCell);

        return listCell;
    }

    @OnEvent(EventType.GROUPS_UPDATE)
    private void onGroupUpdate() {
        List<GroupMember> newMembers = getGroup().getUsers().stream().sorted((t1, t2) -> t1.isOwner() ? -1 : Long.compare(t1.getId(), t2.getId())).toList();
        mainList.getItems().setAll(newMembers);
    }

    public void openUsersModal() {
        modalService.showModal("group/search-users-modal.fxml", Map.of("group", getGroup()));
    }
}
