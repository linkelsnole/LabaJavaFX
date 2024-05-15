package my.snole.laba11.model.ant;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.io.Serializable;


public class WarriorAnt extends Ant implements Serializable {
    private transient Image image;

    private int radius = 20;

    public WarriorAnt() {
        if (image == null) {
            InputStream stream = getClass().getResourceAsStream("/image/war.png");
            if (stream == null) {
                throw new RuntimeException("Cannot find resource file /image/war.png");
            }
            image = new Image(stream);
        }
    }

    @Override
    public Image getImage() {
        return image;
    }

    public int getRadius() {
        return radius;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }
}
