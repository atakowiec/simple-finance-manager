package pl.pollub.frontend.controller.group;

import pl.pollub.frontend.annotation.ViewParameter;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.service.GroupsService;
import pl.pollub.frontend.service.ModalService;

public class RemoveGroupController {
    @ViewParameter("groupId")
    private Long groupId;

    @Inject
    private ModalService modalService;
    @Inject
    private GroupsService groupService;

    public void accept() {
        groupService.removeGroup(groupId);
        modalService.hideModal();
    }

    public void deny() {
        modalService.hideModal();
    }
}
