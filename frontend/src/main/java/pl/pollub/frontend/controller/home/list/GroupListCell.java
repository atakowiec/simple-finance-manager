package pl.pollub.frontend.controller.home.list;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.service.ScreenService;

import java.io.IOException;
import java.util.Map;

public class GroupListCell extends ListCell<Group> {
    private final HBox root;
    private final GroupCellController controller;
    private final ScreenService screenService;

    public GroupListCell(ScreenService screenService) {
        this.screenService = screenService;

        try {
            FXMLLoader loader = new FXMLLoader(FinanceApplication.class.getResource("components/group-list-cell.fxml"));
            root = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(Group item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        controller.setGroup(item);
        setGraphic(root);

        setOnMouseClicked(event -> onClick(item));
    }

    private void onClick(Group group) {
        screenService.switchTo("group", Map.of("groupId", group.getId()));
    }
}
