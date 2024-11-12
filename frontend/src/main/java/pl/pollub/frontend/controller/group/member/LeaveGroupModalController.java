package pl.pollub.frontend.controller.group.member;

import pl.pollub.frontend.controller.group.AbstractGroupController;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.service.GroupsService;
import pl.pollub.frontend.service.ModalService;

public class LeaveGroupModalController extends AbstractGroupController {
    @Inject
    private ModalService modalService;
    @Inject
    private GroupsService groupService;

    public void accept() {
        groupService.leaveGroup(groupId);
        modalService.hideModal();
    }

    public void deny() {
        modalService.hideModal();
    }
}
