package pl.pollub.frontend.manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.annotation.NavBar;
import pl.pollub.frontend.annotation.Title;
import pl.pollub.frontend.controller.MainViewController;
import pl.pollub.frontend.injector.SimpleInjector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    @Getter
    private final Stage stage;
    private final Map<String, String> screenMap = new HashMap<>();

    private final MainViewController mainViewController;

    public ScreenManager(Stage stage) {
        this.stage = stage;

        // on init load main view and prepare hooks for navigation bar and main container elements
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FinanceApplication.class.getResource("main-view.fxml"));
            AnchorPane mainView = fxmlLoader.load();

            mainViewController = fxmlLoader.getController();
            mainViewController.setScreenManager(this);

            stage.setScene(new Scene(mainView, 1100, 650));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addScreen(String name, String path) {
        screenMap.put(name, path);
    }

    public String getScreen(String name) {
        return screenMap.get(name);
    }

    public void switchTo(String screenName) {
        try {
            // load FXML file with new screen and set it as a content of the main container
            FXMLLoader fxmlLoader = new FXMLLoader(FinanceApplication.class.getResource(getScreen(screenName)));

            mainViewController.setContent(fxmlLoader.load());
            this.stage.setTitle("Finance Application");

            Object controller = fxmlLoader.getController();
            if (controller == null) {
                mainViewController.hideNavBar();
                return;
            }

            // set title
            if (controller.getClass().isAnnotationPresent(Title.class)) {
                this.stage.setTitle(controller.getClass().getAnnotation(Title.class).value());
            }

            if (controller.getClass().isAnnotationPresent(NavBar.class)) {
                mainViewController.showNavBar(screenName);
            } else {
                mainViewController.hideNavBar();
            }

            SimpleInjector.inject(controller);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
