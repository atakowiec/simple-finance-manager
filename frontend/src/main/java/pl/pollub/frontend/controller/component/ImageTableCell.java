package pl.pollub.frontend.controller.component;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pl.pollub.frontend.model.transaction.TransactionCategory;

public class ImageTableCell extends TableCell<TransactionCategory, Integer> {
    private final ImageView imageView = new ImageView();

    @Override
    protected void updateItem(Integer categoryId, boolean empty) {
        super.updateItem(categoryId, empty);

        if (empty) {
            setGraphic(null);
            return;
        }

        Image image = new Image("http://localhost:5000/categories/icon/" + categoryId);

        int imageHeight = 20;

        double ratio = image.getWidth() / image.getHeight();
        int imageWidth = (int) (imageHeight * ratio);

        imageView.setImage(image);
        imageView.setFitHeight(20);
        imageView.setFitWidth(imageWidth);

        setGraphic(imageView);
    }
}
