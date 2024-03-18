package my.snole.laba11.model.Ant;

import javafx.scene.image.Image;

import java.io.InputStream;


public class WarriorAnt extends Ant {
    public WarriorAnt() {
        InputStream stream = getClass().getResourceAsStream("/image/war.png");
        if (stream == null) {
            throw new RuntimeException("Cannot find resource file /image/war.png");
        }
        image = new Image(stream);
    }
}
