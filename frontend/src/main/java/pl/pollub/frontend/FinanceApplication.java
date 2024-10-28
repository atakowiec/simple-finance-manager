package pl.pollub.frontend;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.service.AuthService;
import pl.pollub.frontend.service.ScreenService;

public class FinanceApplication extends Application {
    @Inject
    private ScreenService screenService;

    @Inject
    private AuthService authService;

    @Override
    public void start(Stage stage) {
        DependencyInjector injector = DependencyInjector.init(); // init dependecies with reflection
        injector.manualInject(this); // maually inject dependencies on app start

        screenService.init(stage); // init screen manager

        if (authService.tryLogin()) {
            screenService.switchTo("home");
        } else {
            screenService.switchTo("login");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}