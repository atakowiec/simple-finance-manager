package pl.pollub.frontend.controller.group.search;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.ViewParameter;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.service.GroupInviteService;

import java.util.List;

public class SearchUsersController {
    @ViewParameter("group")
    private Group group;

    @Inject
    private GroupInviteService inviteService;
    @Inject
    private DependencyInjector injector;

    @FXML
    private ListView<InviteTarget> mainList;
    @FXML
    public TextField queryInput;

    @PostInitialize
    public void postInitialize() {
        updateAsync();

        mainList.setCellFactory(param -> {
            SearchUserListCell searchUserListCell = new SearchUserListCell(group);
            injector.manualInject(searchUserListCell);
            injector.runPostInitialize(searchUserListCell);
            return searchUserListCell;
        });
    }

    public void updateAsync() {
        Thread thread = new Thread(this::update);
        thread.start();
    }

    public void update() {
        List<InviteTarget> inviteTargets = inviteService.findInviteTargets(group.getId(), queryInput.getText());

        Platform.runLater(() -> mainList.getItems().setAll(inviteTargets));
    }

    public void onInputChange() {
        updateAsync();
    }
}
