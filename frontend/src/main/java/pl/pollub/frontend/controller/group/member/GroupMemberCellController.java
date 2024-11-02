package pl.pollub.frontend.controller.group.member;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.Setter;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.model.group.GroupMember;
import pl.pollub.frontend.service.ModalService;

import java.util.Map;

public class GroupMemberCellController {
    @Inject
    private ModalService modalService;

    @Setter
    private Group group;
    private GroupMember groupMember;

    @FXML
    public Label memberName;
    @FXML
    public Button removeButton;
    @FXML
    public Label memberRole;

    public void setGroupMember(GroupMember groupMember, boolean loggedOwner) {
        this.groupMember = groupMember;
        memberName.setText(groupMember.getUsername());
        memberRole.setText(groupMember.isOwner() ? "Właściciel" : "Członek");

        removeButton.setVisible(loggedOwner && !groupMember.isOwner());
    }

    public void removeMember() {
        modalService.showModal("modal/remove-member-view.fxml", Map.of(
                "group", group,
                "groupMember", groupMember
        ));
    }
}
