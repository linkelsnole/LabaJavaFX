package my.snole.laba11.Ant;

import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public class WorkerAnt extends Ant {
    static private int n1 = 3;
    static private float p1 = 0.4f;

    public WorkerAnt () {
        InputStream stream = getClass().getResourceAsStream("/image/worker.png");
        if (stream == null) {
            throw new RuntimeException("Cannot find resource file /image/worker.png");
        }
        image = new Image(stream);
    }
    static public int getN1() {
        return n1;
    }

    static public float getP1() {
        return p1;
    }
}
