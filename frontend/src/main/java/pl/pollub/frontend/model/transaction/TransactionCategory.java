package pl.pollub.frontend.model.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TransactionCategory {
    private int id;
    private String name;
    private TransactionCategoryType categoryType;
    private byte[] icon;
    private List<TransactionCategory> children = new ArrayList<>();
    private int depth = 0;

    @JsonIgnore
    public Image getImageIcon() {
        return new Image("http://localhost:5000/categories/icon/" + getId());
    }

    @Override
    public String toString() {
        return getName();
    }
}
