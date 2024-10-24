package pl.pollub.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.Setter;
import org.kordamp.bootstrapfx.BootstrapFX;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.manager.ScreenManager;

import java.net.URL;

public class MainViewController {
    @Setter
    private ScreenManager screenManager;

    @FXML
    public StackPane mainContainer;

    @FXML
    public BorderPane navBar;

    @FXML
    public void initialize() {
        mainContainer.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        URL resource = FinanceApplication.class.getResource("css/navbar.css");
        if (resource == null) {
            throw new RuntimeException("Resource not found");
        }
        navBar.getStylesheets().add(resource.toExternalForm());
    }

    public void setContent(Node newContent) {
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(newContent);
    }

    public void hideNavBar() {
        navBar.setVisible(false);
    }

    public void showNavBar(String screenName) {
        navBar.setVisible(true);

        updateNavBar(screenName);
    }

    private void updateNavBar(String name) {
        updateNavBar(navBar, name);
    }

    private void updateNavBar(Pane parent, String name) {
        parent.getChildren().forEach(node -> {
            if (name.equals(node.getId())) {
                if (!node.getStyleClass().contains("active")) {
                    node.getStyleClass().add("active");
                }
            } else {
                node.getStyleClass().remove("active");
            }

            if (node instanceof Pane pane) {
                updateNavBar(pane, name);
            }
        });
    }

    public void onNavItemClick(MouseEvent mouseEvent) {
        if (!(mouseEvent.getSource() instanceof Label label)) {
            return;
        }

        screenManager.switchTo(label.getId());
    }
}
