package my.snole.laba11.Ant;

import my.snole.laba11.IBehaviour;
import javafx.scene.image.Image;
//после буквы Е счкетчик муравьев, доделать чтобы окно скрывалось
//подумать над перезапуском программы
public abstract class Ant implements IBehaviour {
    static protected Image image;

    public Image getImage () {
        return image;
    }
}
