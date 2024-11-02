package pl.pollub.frontend.controller.group;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pl.pollub.frontend.annotation.*;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.service.ScreenService;

import java.util.Map;

@NavBar
@Title("Szczegóły grupy")
@View(name = "group", path = "group/layout.fxml")
public class GroupController {
    private final static Map<String, String> CONTENT_VIEWS = Map.of(
            "transactions", "group/transactions.fxml",
            "members", "group/members.fxml",
            "settings", "group/settings.fxml",
            "add_expense", "group/add-expense.fxml",
            "add_income", "group/add-income.fxml",
            "reports", "group/reports.fxml"
    );

    @ViewParameter("group")
    private Group group;

    @Inject
    private EventEmitter eventEmitter;

    @Inject
    private ScreenService screenService;

    @FXML
    public StackPane contentContainer;
    @FXML
    public VBox navbar;
    @FXML
    public Label groupName;

    private Object contentController = null;

    @PostInitialize
    public void postInitialize() {
        groupName.setText(group.getName());
        setActive("transactions");
    }

    @OnEvent(EventType.GROUPS_UPDATE)
    private void onGroupUpdate() {
        groupName.setText(group.getName());
    }

    public void setActive(String viewName) {
        if (!CONTENT_VIEWS.containsKey(viewName))
            throw new RuntimeException(viewName + " not found");

        FXMLLoader fxmlLoader = screenService.prepareRawView(CONTENT_VIEWS.get(viewName), Map.of("group", group));

        setContentController(fxmlLoader.getController());

        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(fxmlLoader.getRoot());

        refrestSidebar(viewName);
    }

    private void refrestSidebar(String activeViewName) {
        for (String viewName : CONTENT_VIEWS.keySet()) {
            Node node = navbar.lookup("#"+viewName);

            if(node == null)
                continue;

            if(activeViewName.equals(viewName)) {
                node.getStyleClass().add("active");
            } else {
                node.getStyleClass().removeIf(name -> name.equals("active"));
            }
        }
    }

    public void setContentController(Object contentController) {
        if (this.contentController != null)
            eventEmitter.unregisterController(this.contentController);

        this.contentController = contentController;

        if (this.contentController != null)
            eventEmitter.registerController(this.contentController);
    }

    public void onSidenavItemClick(MouseEvent mouseEvent) {
        Node target = (Node) mouseEvent.getSource();

        if(target.getId() == null || !CONTENT_VIEWS.containsKey(target.getId()))
            return;

        setActive(target.getId());
    }
}
