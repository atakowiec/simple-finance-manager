package pl.pollub.frontend.controller.group;

import pl.pollub.frontend.annotation.ViewParameter;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.service.GroupsService;

public abstract class AbstractGroupController {
    @ViewParameter("groupId")
    protected Long groupId;

    @Inject
    protected GroupsService groupsService;

    protected Group getGroup() {
        return groupsService.getGroupById(groupId);
    }
}
