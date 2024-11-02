package pl.pollub.frontend.service;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.controller.component.NotificationController;
import pl.pollub.frontend.controller.group.search.InviteTarget;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;
import pl.pollub.frontend.model.group.GroupInvite;
import pl.pollub.frontend.util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Injectable
public class GroupInviteService {
    @Inject
    private GroupsService groupsService;
    @Inject
    private ScreenService screenService;
    @Inject
    private HttpService httpService;
    @Inject
    private DependencyInjector dependencyInjector;

    private VBox notificationOverlay;
    private VBox notificationContainer;

    private AnchorPane root;

    private final List<GroupInvite> inviteList = new ArrayList<>();

    public List<InviteTarget> findInviteTargets(Long groupId, String query) {
        if (query.isBlank())
            return List.of();

        HttpResponse<String> response = httpService.get("groups/" + groupId + "/users/" + query);

        if (response.statusCode() != 200)
            return List.of();

        Type type = new TypeToken<List<InviteTarget>>() {
        }.getType();

        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    @OnEvent(EventType.WEBSOCKET_MESSAGE)
    private void handleWebsocketMessage(String event, String payload) {
        switch (event) {
            case "set_game_invites" -> setGameInvites(payload);
        }
    }

    public void setRoot(AnchorPane root) {
        this.notificationContainer = (VBox) root.lookup("#notificationContainer");
        this.notificationOverlay = (VBox) root.lookup("#notificationOverlay");
        this.root = root;

        this.notificationOverlay.setOnMouseClicked(event -> hideNotifications());
    }

    private void setGameInvites(String payload) {
        Type type = new TypeToken<List<GroupInvite>>() {
        }.getType();

        List<GroupInvite> inviteList = JsonUtil.GSON.fromJson(payload, type);
        this.inviteList.clear();
        this.inviteList.addAll(inviteList);

        Platform.runLater(this::updateIndicator);
    }

    private void updateIndicator() {
        Node wrapper = root.lookup("#notificationsIndicatorWrapper");
        if (inviteList.isEmpty()) {
            wrapper.setVisible(false);
            wrapper.setManaged(false);
            return;
        }

        wrapper.setVisible(true);
        wrapper.setManaged(true);
        Label inticatorText = (Label) root.lookup("#notificationsIndicator");
        inticatorText.setText(String.valueOf(inviteList.size()));
    }

    private void updateNotifications() {
        Node noNotificationsText = root.lookup("#noNotificationsText");
        noNotificationsText.setVisible(inviteList.isEmpty());
        noNotificationsText.setManaged(inviteList.isEmpty());

        VBox notificationsBox = (VBox) root.lookup("#notifications");
        notificationsBox.setVisible(!inviteList.isEmpty());
        notificationsBox.setManaged(!inviteList.isEmpty());

        if (inviteList.isEmpty())
            return;

        notificationsBox.getChildren().clear();

        for (GroupInvite invite : inviteList) {
            try {
                FXMLLoader loader = new FXMLLoader(FinanceApplication.class.getResource("components/notification.fxml"));
                VBox root = loader.load();
                NotificationController controller = loader.getController();
                dependencyInjector.manualInject(controller);

                controller.setGroupInvite(invite);
                notificationsBox.getChildren().add(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void hideNotifications() {
        notificationOverlay.setVisible(false);
        notificationContainer.setVisible(false);
    }

    public void showNotifications() {
        updateNotifications();

        notificationOverlay.setVisible(true);
        notificationContainer.setVisible(true);
    }

    public void deleteInvite(GroupInvite groupInvite) {
        inviteList.removeIf(otherInvite -> otherInvite.getId().equals(groupInvite.getId()));

        updateNotifications();
        updateIndicator();
    }

    public void onInviteAccept(GroupInvite groupInvite) {
        deleteInvite(groupInvite);

        // todo fetch new group or something because this getGroup() returns group without new member
        screenService.switchTo("group", Map.of("group", groupInvite.getGroup()));
        hideNotifications();
    }
}
