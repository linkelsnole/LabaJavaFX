package my.snole.laba11.model.Ant;

import javafx.scene.image.Image;

import java.io.InputStream;

public class WorkerAnt extends Ant {
    public void setBirthTimeWork(long birthTimeWork) {
        this.birthTime = birthTimeWork;
    }

    public void setLifeTimeWork(long lifeTimeWork) {
        this.lifetime = lifeTimeWork;
    }

    public long birthTime;
    public long lifetime;

    public WorkerAnt() {
        if (image == null) {
            InputStream stream = getClass().getResourceAsStream("/image/worker.png");
            if (stream == null) {
                throw new RuntimeException("Cannot find resource file /image/worker.png");
            }
            image = new Image(stream);
        }
    }
    private static Image image;
    @Override
    public Image getImage() {
        return image;
    }
}
