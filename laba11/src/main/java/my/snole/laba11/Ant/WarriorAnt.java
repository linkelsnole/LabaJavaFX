package my.snole.laba11.Ant;

import javafx.scene.image.Image;

import java.io.File;



public class WarriorAnt extends Ant {
    private int n2 = 5;
    private float p2 = 0.36f;

    public WarriorAnt() {
        image = new Image(new File("/Users/dmitry/Desktop/IDEA-projects/laba11/image/war.png").toURI().toString());
    }

    public int getN2() {
        return n2;
    }

    public float getP2() {
        return p2;
    }
}
