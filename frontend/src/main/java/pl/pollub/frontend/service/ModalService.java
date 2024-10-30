package pl.pollub.frontend.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import pl.pollub.frontend.FinanceApplication;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.injector.DependencyInjector;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;

import java.io.IOException;

@Injectable
@Setter
@Getter
public class ModalService {
    @Inject
    private DependencyInjector injector;

    @Inject
    private EventEmitter eventEmitter;

    @Inject
    private ScreenService screenService;

    private Pane modalOverlay;
    private VBox modalContainer;
    private Object modalController;

    private Thread animationThread;
    private double animationProgress = 0;
    private boolean isShowing = false;

    private void animationFrame() {
        if (modalContainer == null)
            throw new RuntimeException("Modal container not set");

        while (animationProgress <= 1 && animationProgress >= 0) {
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                continue;
            }

            animationProgress += isShowing ? -0.1 : 0.1;
            modalContainer.setTranslateY(animationProgress * 300);
            modalOverlay.setOpacity(1 - animationProgress);

            modalOverlay.setVisible(animationProgress < 0.99);

            if (animationProgress < 0.01) {
                modalContainer.setTranslateY(1);
            }
        }

        try {
            Thread.sleep(20);
            resetModalPosition();
        } catch (InterruptedException ignored) {

        }
    }

    private void resetModalPosition() {
        if (animationProgress < 0.01) {
            modalContainer.setTranslateY(0);
            modalOverlay.setOpacity(1);
        }

        if (animationProgress > 0.99) {
            modalContainer.setTranslateY(300);
            modalOverlay.setOpacity(0);
            modalOverlay.setVisible(false);
        }
    }

    private void runAnimation(boolean show) {
        isShowing = show;
        animationProgress = show ? 1 : 0;

        if (animationThread != null)
            animationThread.interrupt();

        animationThread = new Thread(this::animationFrame);
        animationThread.setDaemon(true);
        animationThread.start();
    }

    public void setModalOverlay(Pane modalOverlay) {
        this.modalOverlay = modalOverlay;

        this.modalOverlay.setOnMouseClicked(event -> {
            Node target = (Node) event.getTarget();
            if (target == modalOverlay || target == modalOverlay.getChildren().get(0)) {
                hideModal();
            }
        });
    }

    public void showModal(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(FinanceApplication.class.getResource(fxmlFile));

            modalContainer.getChildren().clear();
            modalContainer.getChildren().add(loader.load());

            modalController = loader.getController();

            if (modalController != null) {
                injector.manualInject(modalController);
                eventEmitter.registerController(modalController);
                injector.runPostInitialize(modalController);
            }

            runAnimation(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void hideModal() {
        runAnimation(false);

        if (modalController != null) {
            eventEmitter.unregisterController(modalController);
            modalController = null;
        }
    }
}
