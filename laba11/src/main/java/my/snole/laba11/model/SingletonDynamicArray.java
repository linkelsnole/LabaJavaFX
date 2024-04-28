package my.snole.laba11.model;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import my.snole.laba11.Habitat;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.model.ant.WarriorAnt;
import my.snole.laba11.model.ant.WorkerAnt;
import my.snole.laba11.service.Config;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SingletonDynamicArray {
    private static ConcurrentLinkedQueue<Ant> elements = new ConcurrentLinkedQueue<>();
    private static final HashSet<Integer> ids = new HashSet<>();
    private static final TreeMap<Integer, Long> birthTimes = new TreeMap<>();
    private static SingletonDynamicArray instance = null;
    private Config config = new Config();
    private Habitat habitat;


    public static SingletonDynamicArray getInstance() {
        if(instance==null) {
            instance = new SingletonDynamicArray();
        }
        return instance;
    }

    public ConcurrentLinkedQueue<Ant> getAntsList() {
        return elements;
    }
    public Config getConfig() {
        return config;
    }


    public void setAntsList(ConcurrentLinkedQueue<Ant> newAnts) {
        clear();
        for (Ant ant : newAnts) {
            addElementConfig(ant, ant.getBirthTime());
        }
    }


    public void addElement(Ant element, long birthTime) {
        int id = generateUniqueId();
        element.setId(id);
        elements.add(element);
        ids.add(id);
        birthTimes.put(id, birthTime);
        element.setBirthPosition(element.getImageView().getLayoutX(), element.getImageView().getLayoutY());
    }
    public void addElementConfig(Ant element, long birthTime) {
        int id = generateUniqueId();
        element.setId(id);
        elements.add(element);
        ids.add(id);
        birthTimes.put(id, birthTime);
        if (element.getBirthX() == 0 && element.getBirthY() == 0) {
            ImageView imageView = element.getImageView();
            if (imageView != null) {
                element.setBirthPosition(imageView.getLayoutX(), imageView.getLayoutY());
            }
        }
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
            }
        }
    }



    public TreeMap<Integer, Long> getBirthTimes() {
        return new TreeMap<>(birthTimes);
    }

    public Vector<Ant> getElements() {
        return new Vector<>(elements);
    }
    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }





}

