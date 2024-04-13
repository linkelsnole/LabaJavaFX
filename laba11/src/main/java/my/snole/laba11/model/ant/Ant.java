package my.snole.laba11.model.ant;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import my.snole.laba11.model.Point;

import java.util.concurrent.ForkJoinPool;

public abstract class Ant {
    private long birthTime;
    private long lifetime;
    private int id;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public long getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(long birthTime) {
        this.birthTime = birthTime;
    }

    public long getLifetime() {
        return lifetime;
    }

    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    protected Image image;

    public Image getImage () {
        return image;
    }

    private ImageView imageView;

    public ImageView getImageView() {
        return imageView;
    }


    private double birthX;
    private double birthY;

    public double getBirthX() {
        return birthX;
    }

    public double getBirthY() {
        return birthY;
    }

    public void setBirthPosition(double x, double y) {
        this.birthX = x;
        this.birthY = y;
    }


}
