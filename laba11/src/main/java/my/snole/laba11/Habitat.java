package my.snole.laba11;

import javafx.scene.layout.Pane;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.ant.AI.WarriorAntAI;
import my.snole.laba11.model.ant.AI.WorkerAntAI;
import my.snole.laba11.model.Point;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.model.ant.WarriorAnt;
import my.snole.laba11.model.ant.WorkerAnt;
import my.snole.laba11.service.UIService;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;


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
    private Pane scenePane;
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


    public Habitat(Pane scene) {
        this.scenePane = scene;
    }




    public void update(long time, long workerAntN1, long warriorAntN2, float workerAntP1, float warriorAntP2, long workLifeTime, long warLifeTime, boolean isWorkerAIChecked, boolean isWarriorAIChecked) {
        if (!simulationActive) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        list.removeExpiredElements(currentTime, scenePane);

        if (time % workerAntN1 == 0 && service.checkProbability(workerAntP1)) {
            WorkerAnt workerAnt = new WorkerAnt();
            workerAnt.setId(list.generateUniqueId());
            workerAnt.setBirthTime(currentTime);
            workerAnt.setLifetime(workLifeTime);
            setAnt(workerAnt);
            SingletonDynamicArray.getInstance().addElement(workerAnt, workerAnt.getBirthTime());
            System.out.println("workerAnt");
            workerAntcount++;
            if (isWorkerAIChecked) {
                WorkerAntAI workerAntAI = new WorkerAntAI(workerAnt, scenePane.getWidth(), scenePane.getHeight());
                workerAntAI.start();
                setWorkerAntAI(workerAntAI);
            }

        }

        if (time % warriorAntN2 == 0 && service.checkProbability(warriorAntP2)) {
            WarriorAnt warriorAnt = new WarriorAnt();
            warriorAnt.setId(list.generateUniqueId());
            warriorAnt.setBirthTime(currentTime);
            warriorAnt.setLifetime(warLifeTime);
            setAnt(warriorAnt);
            SingletonDynamicArray.getInstance().addElement(warriorAnt, warriorAnt.getBirthTime());
            System.out.println("warAnt");
            warriorAntcount++;
            if (isWarriorAIChecked) {
                double centerX = warriorAnt.getBirthX();
                double centerY = warriorAnt.getBirthY();
                double radius = 20;
                WarriorAntAI warriorAntAI = new WarriorAntAI(warriorAnt, centerX, centerY, radius);
                warriorAntAI.start();
                setWarriorAntAI(warriorAntAI);
            }
        }

//        System.out.println("scenePane layout: x=" + scenePane.getLayoutX() + ", y=" + scenePane.getLayoutY());
//        System.out.println("scenePane size: width=" + scenePane.getWidth() + ", height=" + scenePane.getHeight());
    }


    private void setAnt(Ant ant) {
        Point point = service.generateRandomPoint();
        ImageView imageView = new ImageView(ant.getImage());
        imageView.setLayoutX(point.getX());
        imageView.setLayoutY(point.getY());
        imageView.setVisible(true);
        scenePane.getChildren().add(imageView);
        ant.setImageView(imageView);
        ant.setBirthPosition(point.getX(), point.getY());
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
        //ai
        BaseAI.stopAllAI();
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


    private WorkerAntAI workerAntAI;
    private WarriorAntAI warriorAntAI;

    public void setWorkerAntAI(WorkerAntAI workerAntAI) {
        this.workerAntAI = workerAntAI;
    }

    public void setWarriorAntAI(WarriorAntAI warriorAntAI) {
        this.warriorAntAI = warriorAntAI;
    }

    public void changeWorkerAntPriority(int newPriority) {
        if(workerAntAI != null) {
            workerAntAI.setAIPriority(newPriority);
        }
    }

    public void changeWarriorAntPriority(int newPriority) {
        if(warriorAntAI != null) {
            warriorAntAI.setAIPriority(newPriority);
        }
    }
}