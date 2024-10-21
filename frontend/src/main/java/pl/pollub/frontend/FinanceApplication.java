package pl.pollub.frontend;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.pollub.frontend.injector.SimpleInjector;
import pl.pollub.frontend.manager.AuthManager;
import pl.pollub.frontend.manager.HttpManager;
import pl.pollub.frontend.manager.ScreenManager;

public class FinanceApplication extends Application {

    @Override
    public void start(Stage stage) {
        initInjector(stage);
        initApp();
    }

    private void initInjector(Stage stage) {
        ScreenManager screenManager = new ScreenManager(stage);
        AuthManager authManager = new AuthManager();

        SimpleInjector.addInstance(screenManager);
        SimpleInjector.addInstance(new HttpManager(authManager));
        SimpleInjector.addInstance(authManager);
    }

    private void initApp() {
        ScreenManager screenManager = SimpleInjector.getInstance(ScreenManager.class);
        screenManager.getStage().setResizable(false);

        initViews(screenManager);

        screenManager.switchTo("register"); // default screen
    }

    private void initViews(ScreenManager screenManager) {
        screenManager.addScreen("register", "register-view.fxml");
        screenManager.addScreen("home", "home-view.fxml");
    }

    public static void main(String[] args) {
        launch();
    }
}