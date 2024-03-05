package my.snole.laba11.Ant;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class WorkerAnt extends Ant {
    static private int n1 = 3;
    static private float p1 = 0.45f;

    public WorkerAnt () {
        image = new Image(new File("/Users/dmitry/Desktop/IDEA-projects/laba11/image/worker.png").toURI().toString());
    }

    public int getN1() {
        return n1;
    }

    public float getP1() {
        return p1;
    }
}
