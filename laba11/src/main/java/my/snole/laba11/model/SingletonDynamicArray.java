package my.snole.laba11.model;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.ant.Ant;

import java.util.*;

public class SingletonDynamicArray {

    private static final Vector<Ant> elements = new Vector<>();
    private static final HashSet<Integer> ids = new HashSet<>();
    private static final TreeMap<Integer, Long> birthTimes = new TreeMap<>();
    private static SingletonDynamicArray instance = null;
    private SingletonDynamicArray() {
    }

    public static SingletonDynamicArray getInstance() {
        if(instance==null) {
            instance = new SingletonDynamicArray();
        }
        return instance;
    }

public void addElement(Ant element, long birthTime) {
    int id = generateUniqueId();
    element.setId(id);
    elements.add(element);
    ids.add(id);
    birthTimes.put(id, birthTime);
    element.setBirthPosition(element.getImageView().getLayoutX(), element.getImageView().getLayoutY());
}

    public void clear() {
        elements.clear();
        ids.clear();
        birthTimes.clear();
    }

    public int generateUniqueId() {
        Random rand = new Random();
        int id = rand.nextInt(Integer.MAX_VALUE);
        while (ids.contains(id)) {
            id = rand.nextInt(Integer.MAX_VALUE);
        }
        return id;
    }

    public void removeExpiredElements(long currentTime, Pane scene) {
        Iterator<Ant> iterator = elements.iterator();
        while (iterator.hasNext()) {
            Ant ant = iterator.next();
            if ((currentTime - ant.getBirthTime()) >= ant.getLifetime() * 1000) {
                if (ant.getImageView() != null) {
                    Platform.runLater(() -> scene.getChildren().remove(ant.getImageView()));
                }
                iterator.remove();
                System.out.println("Сработало удаление для муравья с ID: " + ant.getId());
            }
        }
    }

    public TreeMap<Integer, Long> getBirthTimes() {
        return new TreeMap<>(birthTimes);
    }

    public Vector<Ant> getElements() {
        return new Vector<>(elements);
    }


}

