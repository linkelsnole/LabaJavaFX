package my.snole.laba11.Ant;

import javafx.scene.image.ImageView;
import my.snole.laba11.IBehaviour;
import javafx.scene.image.Image;

public abstract class Ant implements IBehaviour {
    static protected Image image;
    static protected ImageView imageView;

    public Image getImage () {return image;}

    public ImageView getImageView() {
        if (imageView == null) {
            imageView = new ImageView(getImage());
        }
        return imageView;
    }
}
