package pl.pollub.frontend.controller.home;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import pl.pollub.frontend.annotation.NavBar;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.Title;
import pl.pollub.frontend.annotation.View;
import pl.pollub.frontend.controller.home.list.GroupListCell;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.service.GroupsService;
import pl.pollub.frontend.service.ModalService;
import pl.pollub.frontend.service.ScreenService;

@NavBar()
@Title("Strona główna")
@View(name = "home", path = "home-view.fxml")
public class HomeController {
    @FXML
    public ListView<Group> mainList;

    @Inject
    private GroupsService groupsService;
    @Inject
    private ScreenService screenService;
    @Inject
    private ModalService modalService;

    @PostInitialize
    public void postInitialize() {
        onGroupsUpdate();

        mainList.setCellFactory(param -> new GroupListCell(screenService));
    }

    @OnEvent(EventType.GROUPS_UPDATE)
    private void onGroupsUpdate() {
        mainList.getItems().setAll(groupsService.getGroups());

        mainList.getItems().sort((t1, t2) -> t2.getId().compareTo(t1.getId()));
    }

    public void openAddGroupModal() {
        modalService.showModal("modal/add-group-view.fxml");
    }
}
