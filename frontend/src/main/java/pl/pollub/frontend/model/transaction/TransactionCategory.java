package pl.pollub.frontend.model.transaction;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import pl.pollub.frontend.FinanceApplication;

import java.net.URL;

@Setter
@Getter
public class TransactionCategory {
    private int id;
    private String name;
    private String icon;

    public Image getIcon() {
        URL resource = FinanceApplication.class.getResource("images/categories/"+icon);
        if(resource == null)
            return null;

        return new Image(resource.toExternalForm());
    }

    @Override
    public String toString() {
        return getName();
    }
}
