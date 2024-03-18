package my.snole.laba11.model.Ant;

import my.snole.laba11.IBehaviour;
import javafx.scene.image.Image;

public abstract class Ant implements IBehaviour {
    static protected Image image;

    public Image getImage () {
        return image;
    }
}
