package pl.pollub.frontend.controller.group.search;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;

import java.io.IOException;

public class SearchUserListCell extends ListCell<InviteTarget> {
    @Inject
    private DependencyInjector dependencyInjector;

    private final HBox root;
    private final SearchUserCellContoller controller;

    public SearchUserListCell(Group group) {
        try {
            FXMLLoader loader = new FXMLLoader(FinanceApplication.class.getResource("components/search-user-list-cell.fxml"));
            root = loader.load();
            controller = loader.getController();
            controller.setGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostInitialize
    public void postInitialize() {
        dependencyInjector.manualInject(controller);
    }

    @Override
    protected void updateItem(InviteTarget item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {

            setGraphic(null);
            return;
        }

        controller.setInviteTarget(item);
        setGraphic(root);
    }
}
