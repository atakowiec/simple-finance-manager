package pl.pollub.frontend.controller.group.member;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.model.group.GroupMember;
import pl.pollub.frontend.service.AuthService;

import java.io.IOException;
import java.util.Objects;

public class GroupMemberListCell extends ListCell<GroupMember> {
    @Inject
    private DependencyInjector dependencyInjector;
    @Inject
    private AuthService authService;

    private final Group group;
    private final HBox root;
    private final GroupMemberCellController controller;

    public GroupMemberListCell(Group group) {
        this.group = group;

        try {
            FXMLLoader loader = new FXMLLoader(FinanceApplication.class.getResource("components/member-list-cell.fxml"));
            root = loader.load();
            controller = loader.getController();
            controller.setGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostInitialize
    public void postInitialize() {
        dependencyInjector.manualInject(controller);
    }

    @Override
    protected void updateItem(GroupMember item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {

            setGraphic(null);
            return;
        }

        boolean ownerLogged = Objects.equals(group.getOwner().getId(), authService.getUser().getId());

        controller.setGroupMember(item, ownerLogged);
        setGraphic(root);
    }
}
