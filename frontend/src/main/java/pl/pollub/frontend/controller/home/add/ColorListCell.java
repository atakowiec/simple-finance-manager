package pl.pollub.frontend.controller.home.add;

import javafx.scene.control.ListCell;

public class ColorListCell extends ListCell<String> {
    public ColorListCell() {
        this(null);
    }

    public ColorListCell(String color) {
        if(color != null)
            updateItem(color, false);
    }

    @Override
    protected void updateItem(String color, boolean empty) {
        super.updateItem(color, empty);

        if (empty || color == null) {
            setGraphic(null);
            return;
        }

        setStyle("-fx-background-color: " + color);
    }
}
