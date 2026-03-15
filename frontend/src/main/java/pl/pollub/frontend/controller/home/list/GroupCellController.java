package pl.pollub.frontend.controller.home.list;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import lombok.Getter;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.service.GroupsService;

@Getter
public class GroupCellController {
    private Group group;

    @Inject
    private GroupsService groupsService;

    @FXML
    private Circle groupBackground;

    @FXML
    private ImageView groupIconImage;

    @FXML
    private Label nameLabel;

    @FXML
    private Label membersLabel;

    public void setGroup(Group group) {
        this.group = group;

        nameLabel.setText(group.getName());
        membersLabel.setText(group.getUsers().size() + " członków");
        groupBackground.setFill(Paint.valueOf(group.getColor()));

        groupIconImage.setClip(new Circle(25, 25, 25));
        groupIconImage.setImage(groupsService.getGroupIconImage(group));
        groupIconImage.setVisible(group.isHasIcon());
        groupIconImage.setManaged(group.isHasIcon());
    }
}
