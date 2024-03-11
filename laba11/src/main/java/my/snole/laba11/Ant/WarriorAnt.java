package my.snole.laba11.Ant;

import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;


public class WarriorAnt extends Ant {
    static private int n2 = 5;
    static private float p2 = 0.3f;

    public WarriorAnt() {
        InputStream stream = getClass().getResourceAsStream("/image/war.png");
        if (stream == null) {
            throw new RuntimeException("Cannot find resource file /image/war.png");
        }
        image = new Image(stream);
    }

    static public int getN2() {
        return n2;
    }

    static public float getP2() {
        return p2;
    }
}
