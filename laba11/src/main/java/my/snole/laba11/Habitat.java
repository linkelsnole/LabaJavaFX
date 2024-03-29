package my.snole.laba11;

import javafx.application.Platform;
import my.snole.laba11.model.Point;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.Ant.Ant;
import my.snole.laba11.model.Ant.WarriorAnt;
import my.snole.laba11.model.Ant.WorkerAnt;
import my.snole.laba11.service.UIService;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;

import java.util.Iterator;


public class Habitat  {

    public static  int warriorAntcount = 0;
    public static  int workerAntcount = 0;
    public static boolean eKeyPressed = false;

    public static long startTime;
    public static long stoptime = 0;
    public static boolean isSimulationStopped = false;
    public static boolean simulationActive;
    public static SingletonDynamicArray list;
    private final UIService service = new UIService();
    private AnchorPane scene;
    private HabitatListener listener;
    public SimulationStateListener stateListener;


    // Интерфейсы
    public interface SimulationStateListener {
        void onSimulationStarted();
        void onSimulationStopped();
    }
    @FunctionalInterface
    public interface HabitatListener {
        void onAntAdded(ImageView imageView);
    }


    public Habitat(AnchorPane scene) {
        this.scene = scene;
    }

    public void update(long time, long workerAntN1, long warriorAntN2, float workerAntP1, float warriorAntP2, long workLifeTime, long warLifeTime) {
        if (!simulationActive) {
            return;
        }
        long currentTime = System.currentTimeMillis();

        list.removeExpiredElements(currentTime, scene);

        if (time % workerAntN1 == 0 && service.checkProbability(workerAntP1)) {
            WorkerAnt workerAnt = new WorkerAnt();
            workerAnt.setId(list.generateUniqueId());
            workerAnt.setBirthTime(currentTime);
            workerAnt.setLifetime(workLifeTime);
            SingletonDynamicArray.getInstance().addElement(workerAnt, workerAnt.getBirthTime());
            setAnt(workerAnt);
            System.out.println("workerAnt");
            workerAntcount++;
        }

        if (time % warriorAntN2 == 0 && service.checkProbability(warriorAntP2)) {
            WarriorAnt warriorAnt = new WarriorAnt();
            warriorAnt.setId(list.generateUniqueId());
            warriorAnt.setBirthTime(currentTime);
            warriorAnt.setLifetime(warLifeTime);
            SingletonDynamicArray.getInstance().addElement(warriorAnt, warriorAnt.getBirthTime());
            setAnt(warriorAnt);
            System.out.println("warAnt");
            warriorAntcount++;
        }
    }


    private void setAnt(Ant ant) {
        Point point = service.generateRandomPoint();
        ImageView imageView = new ImageView(ant.getImage());
        imageView.setX(point.getX());
        imageView.setY(point.getY());
        imageView.setVisible(true);
        scene.getChildren().add(imageView);
        ant.setImageView(imageView);
        if (listener != null) {
            listener.onAntAdded(imageView);
        }
    }

    public void startSimulation() {
        eKeyPressed = false;
        startTime = System.currentTimeMillis();
        list = SingletonDynamicArray.getInstance();
        simulationActive = true;
        if (stateListener != null) {
            stateListener.onSimulationStarted();
        }
    }

    public void stopSimulation() {
        simulationActive = false;
        eKeyPressed = true;
        if (stateListener != null) {
            stateListener.onSimulationStopped();
        }
    }

    // Сеттеры
    public void setSimulationStateListener(SimulationStateListener listener) {
        this.stateListener = listener;
    }

    public void setHabitatListener(HabitatListener listener) {
        this.listener = listener;
    }


}