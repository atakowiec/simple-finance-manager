package pl.pollub.frontend.controller.component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.GroupInvite;
import pl.pollub.frontend.service.GroupInviteService;
import pl.pollub.frontend.service.HttpService;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationController {
    @Inject
    private HttpService httpService;
    @Inject
    private GroupInviteService groupInviteService;

    @FXML
    public Text groupName;
    @FXML
    public Label date;

    private GroupInvite groupInvite;

    public void setGroupInvite(GroupInvite groupInvite) {
        this.groupInvite = groupInvite;

        groupName.setText(groupInvite.getGroup().getName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        LocalDateTime createdData = LocalDateTime.parse(groupInvite.getCreatedAt());
        date.setText(createdData.format(formatter));
    }

    public void accept() {
        HttpResponse<String> response = httpService.post("groups/invite/" + groupInvite.getId() + "/accept", null);

        if(response.statusCode() != 200)
            return; // todo someday we can add some kind of alerts here

        groupInviteService.onInviteAccept(groupInvite);
    }

    public void deny() {
        HttpResponse<String> response = httpService.delete("groups/invite/" + groupInvite.getId() + "/deny");

        if(response.statusCode() != 200)
            return;

        groupInviteService.deleteInvite(groupInvite);
    }
}
