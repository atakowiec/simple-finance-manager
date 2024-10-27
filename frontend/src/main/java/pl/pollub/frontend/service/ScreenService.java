package pl.pollub.frontend.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.annotation.NavBar;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.Title;
import pl.pollub.frontend.annotation.View;
import pl.pollub.frontend.controller.MainViewController;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Injectable
public class ScreenService {
    private final Map<String, String> screenMap = new HashMap<>();

    @Inject
    private DependencyInjector dependencyInjector;

    @Getter
    private Stage stage;
    private MainViewController mainViewController;

    public void init(Stage stage) {
        this.stage = stage;
        this.initViews();

        // on init load main view and prepare hooks for navigation bar and main container elements
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FinanceApplication.class.getResource("main-view.fxml"));
            AnchorPane mainView = fxmlLoader.load();

            mainViewController = fxmlLoader.getController();
            dependencyInjector.manualInject(mainViewController);

            stage.setScene(new Scene(mainView, 1100, 650));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initViews() {
        Reflections reflections = new Reflections("pl.pollub.frontend", Scanners.TypesAnnotated);

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(View.class);

        for (Class<?> clazz : annotatedClasses) {
            View viewAnnotation = clazz.getAnnotation(View.class);

            addScreen(viewAnnotation.name(), viewAnnotation.path());
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

            dependencyInjector.manualInject(controller);

            runPostInitialize(controller);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runPostInitialize(Object controller) {
        for (Method method : controller.getClass().getMethods()) {
            if (!method.isAnnotationPresent(PostInitialize.class)) {
                continue;
            }

            try {
                method.invoke(controller);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}