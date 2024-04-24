package my.snole.laba11.model.ant;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.io.Serializable;

public class WorkerAnt extends Ant implements Serializable {
    private transient Image image;
    public WorkerAnt() {
        if (image == null) {
            InputStream stream = getClass().getResourceAsStream("/image/worker.png");
            if (stream == null) {
                throw new RuntimeException("Cannot find resource file /image/worker.png");
            }
            image = new Image(stream);
        }
    }
    @Override
    public Image getImage() {
        return image;
    }
}
