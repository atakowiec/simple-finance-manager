package pl.pollub.frontend.controller.group.member;

import javafx.scene.control.Alert;
import pl.pollub.frontend.annotation.ViewParameter;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.model.group.GroupMember;
import pl.pollub.frontend.service.HttpService;
import pl.pollub.frontend.service.ModalService;

import java.net.http.HttpResponse;
import java.util.Objects;

public class RemoveMemberModalController {
    @Inject
    private HttpService httpService;
    @Inject
    private ModalService modalService;
    @Inject
    private EventEmitter eventEmitter;

    @ViewParameter("group")
    private Group group;
    @ViewParameter("groupMember")
    private GroupMember groupMember;

    public void accept() {
        HttpResponse<String> response = httpService.delete("/groups/" + group.getId() + "/member/" + groupMember.getId());

        modalService.hideModal();

        if(response.statusCode() != 200) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setContentText("Wystąpił błąd podczas usuwania członka grupy. Spróbuj ponownie później.");
            alert.show();
            return;
        }

        group.getUsers().removeIf(otherMember -> Objects.equals(otherMember.getId(), groupMember.getId()));
        eventEmitter.emit(EventType.GROUPS_UPDATE);
    }

    public void deny() {
        modalService.hideModal();
    }
}
