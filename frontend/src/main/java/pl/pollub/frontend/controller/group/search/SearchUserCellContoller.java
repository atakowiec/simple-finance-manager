package pl.pollub.frontend.controller.group.search;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.Setter;
import pl.pollub.frontend.enums.MembershipStatus;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.service.HttpService;

import java.net.http.HttpResponse;

public class SearchUserCellContoller {
    @FXML
    public Label memberName;
    @FXML
    public Button button;

    @Inject
    private HttpService httpService;

    @Setter
    private Group group;

    private InviteTarget inviteTarget;

    public void setInviteTarget(InviteTarget inviteTarget) {
        this.inviteTarget = inviteTarget;
        memberName.setText(inviteTarget.getUsername());

        if (inviteTarget.getMembershipStatus() == MembershipStatus.NONE) {
            button.setText("Zaproś");
            button.getStyleClass().setAll("inviteButton");
            return;
        }

        if (inviteTarget.getMembershipStatus() == MembershipStatus.INVITED) {
            button.setText("Zaproszony");
            button.getStyleClass().setAll("invitedButton");
            return;
        }

        button.setText("W grupie");
        button.getStyleClass().setAll("inGroupButton");
    }

    public void buttonClick() {
        if (inviteTarget.getMembershipStatus() == MembershipStatus.IN_GROUP)
            return;

        HttpResponse<String> response;
        if (inviteTarget.getMembershipStatus() == MembershipStatus.INVITED) {
            response = httpService.delete("groups/" + group.getId() + "/invite/" + inviteTarget.getId());
        } else {
            response = httpService.post("groups/" + group.getId() + "/invite/" + inviteTarget.getId(), null);
        }

        if (response.statusCode() != 200) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd podczas zapraszania użytkownika");
            alert.setContentText(response.body());
            alert.showAndWait();
            return;
        }

        if (inviteTarget.getMembershipStatus() == MembershipStatus.INVITED) {
            inviteTarget.setMembershipStatus(MembershipStatus.NONE);
        } else {
            inviteTarget.setMembershipStatus(MembershipStatus.INVITED);
        }

        setInviteTarget(inviteTarget); // this will update button text and style
    }
}
