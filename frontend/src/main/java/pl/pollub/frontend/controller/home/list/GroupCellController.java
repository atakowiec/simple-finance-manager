package pl.pollub.frontend.controller.home.list;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import lombok.Getter;
import pl.pollub.frontend.model.group.Group;

@Getter
public class GroupCellController {
    private Group group;

    @FXML
    private Circle groupIcon;

    @FXML
    private Label nameLabel;

    @FXML
    private Label membersLabel;

    public void setGroup(Group group) {
        this.group = group;

        nameLabel.setText(group.getName());
        membersLabel.setText(group.getUsers().size() + " członków");
        groupIcon.setFill(Paint.valueOf(group.getColor()));
    }
}
