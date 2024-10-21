package pl.pollub.frontend.manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.kordamp.bootstrapfx.BootstrapFX;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.injector.SimpleInjector;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ScreenManager {
    @Getter
    private final Stage stage;
    private final Map<String, String> screenMap = new HashMap<>();

    public void addScreen(String name, String path) {
        screenMap.put(name, path);
    }

    public String getScreen(String name) {
        return screenMap.get(name);
    }

    public void switchTo(String name) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FinanceApplication.class.getResource(getScreen(name)));
            Scene scene = new Scene(fxmlLoader.load(), 1100, 650);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            stage.setScene(scene);
            stage.show();

            // simple dependency injection
            Object controller = fxmlLoader.getController();
            if(controller == null) {
                return;
            }

            for (Field field : controller.getClass().getFields()) {
                Object instance = SimpleInjector.getInstance(field.getType());
                if (instance != null) {
                    field.set(controller, instance);
                }
            }
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
