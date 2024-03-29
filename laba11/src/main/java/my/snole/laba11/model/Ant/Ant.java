package my.snole.laba11.model.Ant;

import javafx.scene.image.ImageView;
import my.snole.laba11.IBehaviour;
import javafx.scene.image.Image;

public abstract class Ant implements IBehaviour {
    private long birthTime;
    private long lifetime;
    private int id;

    public void setImage(Image image) {
        this.image = image;
    }

    protected Image image;


    // Геттеры и сеттеры
    public Image getImage () {
        return image;
    }
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

    private ImageView imageView;

    public ImageView getImageView() {
        return imageView;
    }
}
