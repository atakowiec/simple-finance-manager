package pl.pollub.frontend.model.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionCategory {
    private int id;
    private String name;
    private TransactionCategoryType categoryType;
    private byte[] icon;

    @JsonIgnore
    public Image getImageIcon() {
        return new Image("http://localhost:5000/categories/icon/" + getId());
    }

    @Override
    public String toString() {
        return getName();
    }
}
