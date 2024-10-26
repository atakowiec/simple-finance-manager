package pl.pollub.frontend;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.service.ScreenService;

public class FinanceApplication extends Application {
    @Inject
    private ScreenService screenService;

    @Override
    public void start(Stage stage) {
        DependencyInjector injector = DependencyInjector.init(); // init dependecies with reflection
        injector.manualInject(this); // maually inject screen manager on app start

        screenService.init(stage); // init screen manager

        screenService.switchTo("home"); // default screen
    }

    public static void main(String[] args) {
        launch();
    }
}