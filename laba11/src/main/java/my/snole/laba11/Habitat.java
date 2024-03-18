package my.snole.laba11;

import my.snole.laba11.UIController.UIController;
import my.snole.laba11.model.Point;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.Ant.Ant;
import my.snole.laba11.model.Ant.WarriorAnt;
import my.snole.laba11.model.Ant.WorkerAnt;
import my.snole.laba11.service.UIService;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;

import java.util.Timer;


public class Habitat  {

    public interface SimulationStateListener {
        void onSimulationStarted();
        void onSimulationStopped();
    }

    public static  int warriorAntcount = 0;
    public static  int workerAntcount = 0;
    public static boolean eKeyPressed = false;

    public static long startTime;
    public static long stoptime = 0;
    public static boolean isSimulationStopped = false;
    public static boolean simulationActive;
    private SingletonDynamicArray list;
    private final UIService service = new UIService();
    private AnchorPane scene;

    private HabitatListener listener;
    public SimulationStateListener stateListener;

    public void setSimulationStateListener(SimulationStateListener listener) {
        this.stateListener = listener;
    }

    public void setHabitatListener(HabitatListener listener) {
        this.listener = listener;
    }

    public Habitat(AnchorPane scene) {
        this.scene = scene;
    }

    public void update(long time, long workerAntN1, long warriorAntN2, float workerAntP1, float warriorAntP2) {
        if (!simulationActive) {
            return;
        }

        if (time % workerAntN1 == 0 && service.checkProbability(workerAntP1)) {
            WorkerAnt workerAnt = new WorkerAnt();
            list.addElement(workerAnt);
            setAnt(workerAnt);
            System.out.println("workerAnt");
            workerAntcount++;
        }

        if (time % warriorAntN2 == 0 && service.checkProbability(warriorAntP2)) {
            WarriorAnt warriorAnt = new WarriorAnt();
            list.addElement(warriorAnt);
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

    public void stopSimulation() {//перенести
        simulationActive = false;
        eKeyPressed = true;
//        if (showWindow.isSelected()) {
//            showStopSimulationDialog();
//        } else {
//            actuallyStopSimulation();
//        }
        if (stateListener != null) {
            stateListener.onSimulationStopped();
        }
    }

//    public void actuallyStopSimulation() {//перенести
//        clearListAndTask();
//        updateTimeLabel();
//        popup.hide();
//        simulationActive = false;
//        if (stateListener != null) {
//            stateListener.onSimulationStopped();
//        }
//    }


    // Вспомогательные методы

}