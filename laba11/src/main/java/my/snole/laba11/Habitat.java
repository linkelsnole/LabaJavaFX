package my.snole.laba11;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.ant.AI.WarriorAntAI;
import my.snole.laba11.model.ant.AI.WorkerAntAI;
import my.snole.laba11.model.Point;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.model.ant.WarriorAnt;
import my.snole.laba11.model.ant.WorkerAnt;
import my.snole.laba11.service.Config;
import my.snole.laba11.service.UIService;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;

import java.util.concurrent.ConcurrentLinkedQueue;


public class Habitat  {

    public static  int warriorAntcount = 0;
    public static  int workerAntcount = 0;
    public static boolean eKeyPressed = false;
    public static long currentTimeSimulation;
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
    public WarriorAntAI warriorAntAI;
    public WorkerAntAI workerAntAI;
    private long workerAntN1;
    private long warriorAntN2;
    private float workerAntP1;
    private float warriorAntP2;
    private long workLifeTime;
    private long warLifeTime;
    private Image imageWork = new Image(getClass().getResourceAsStream("/image/worker.png"));
    private Image imageWar = new Image(getClass().getResourceAsStream("/image/war.png"));



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
        SingletonDynamicArray.getInstance().getConfig().setHabitat(this);
    }


    public long getLoadTIme() {
        return loadTIme;
    }

    private long loadTIme;

    public void update(long time, long workerAntN1, long warriorAntN2, float workerAntP1, float warriorAntP2, long workLifeTime, long warLifeTime) {
        if (!simulationActive) {
            return;
        }
        this.workerAntN1 = workerAntN1;
        this.warriorAntN2 = warriorAntN2;
        this.workerAntP1 = workerAntP1;
        this.warriorAntP2 = warriorAntP2;
        this.workLifeTime = workLifeTime;
        this.warLifeTime = warLifeTime;
        loadTIme = time;
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
        }

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


    public void restoreAntImageView(Ant ant) { if (ant.getBirthX() != 0 && ant.getBirthY() != 0) {
        ImageView imageView = new ImageView(ant instanceof WorkerAnt ? imageWork : imageWar);
        imageView.setLayoutX(ant.getBirthX());
        imageView.setLayoutY(ant.getBirthY());
        imageView.setVisible(true);
        scenePane.getChildren().add(imageView);
        ant.setImageView(imageView);
    }}

    public void startSimulation() {
        eKeyPressed = false;
        if (Config.isLoadedFromSave) {
            startTime = System.currentTimeMillis() - currentTimeSimulation;
            Config.isLoadedFromSave = false;
        } else {
            startTime = System.currentTimeMillis();
        }
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

    public void toggleWarriorAntAI(boolean enable) {
        if (enable) {
            if (warriorAntAI == null || !warriorAntAI.isAlive()) {
                warriorAntAI = new WarriorAntAI();
                warriorAntAI.startAI();
            } else {
                warriorAntAI.resumeAI();
            }
        } else {
            if (warriorAntAI != null) {
                warriorAntAI.pauseAI();
            }
        }
    }

    public void toggleWorkerAntAI(boolean enable) {
        if (enable) {
            if (workerAntAI == null || !workerAntAI.isAlive()) {
                workerAntAI = new WorkerAntAI(scenePane.getWidth(), scenePane.getHeight());
                workerAntAI.startAI();
            } else {
                workerAntAI.resumeAI();
            }
        } else {
            if (workerAntAI != null) {
                workerAntAI.pauseAI();
            }
        }
    }

    public void stopAnts () {
        if (warriorAntAI != null && workerAntAI != null) {
            warriorAntAI.stopAI();
            workerAntAI.stopAI();
        }
    }



    public long getCurrentTimeSimulation() {
        return currentTimeSimulation;
    }

    public long getWorkerAntN1() {
        return workerAntN1;
    }

    public long getWarriorAntN2() {
        return warriorAntN2;
    }

    public float getWorkerAntP1() {
        return workerAntP1;
    }

    public float getWarriorAntP2() {
        return warriorAntP2;
    }

    public long getWorkLifeTime() {
        return workLifeTime;
    }

    public long getWarLifeTime() {
        return warLifeTime;
    }

    public void setCurrentTimeSimulation(long currentTimeSimulation) {
        this.currentTimeSimulation = currentTimeSimulation;
    }


}