package my.snole.laba11.service;

import java.io.Serializable;

import java.io.*;

import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import my.snole.laba11.Habitat;
import my.snole.laba11.UIController.UIController;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.ant.AI.WorkerAntAI;
import my.snole.laba11.model.ant.Ant;

public class Config implements Serializable {
    private Habitat habitat;
    private UIController uiController;
    public static boolean isLoadedFromSave = false;
    private File file = new File("serialized.dat");
    private long loadedSimulationTime;


    /**
     * Сериализует текущее состояние симуляции в файл [[config.txt]]
     */
    public synchronized void saveInFile() {
        try (FileOutputStream fileOutputStream = new FileOutputStream("config.txt");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {


            objectOutputStream.writeObject(uiController.getWorkerAntN1());
            objectOutputStream.writeObject(uiController.getWarriorAntN2());
            objectOutputStream.writeObject(uiController.getWorkerAntP1());
            objectOutputStream.writeObject(uiController.getWarriorAntP2());
            objectOutputStream.writeObject(uiController.getWorkLifeTime());
            objectOutputStream.writeObject(uiController.getWarLifeTime());
            objectOutputStream.writeObject(uiController.workerAI.isSelected());
            objectOutputStream.writeObject(uiController.warriorAI.isSelected());


        } catch (FileNotFoundException e) {
            showAlert("Error! Config file not found!");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error writing to config file!");
        }
    }


    /**
     * Десериализует состояние симуляции из файла [[config.txt]].
     */
    public synchronized void loadFromFile() {
        try (FileInputStream fileInputStream = new FileInputStream("config.txt");
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            long workerAntN1 = (long) objectInputStream.readObject();
            long warriorAntN2 = (long) objectInputStream.readObject();
            float workerAntP1 = (float) objectInputStream.readObject();
            float warriorAntP2 = (float) objectInputStream.readObject();
            long workLifeTime = (long) objectInputStream.readObject();
            long warLifeTime = (long) objectInputStream.readObject();
            boolean workerAI = (boolean) objectInputStream.readObject();
            boolean warriorAi = (boolean) objectInputStream.readObject();


            uiController.setSimulationParameters(workerAntN1, warriorAntN2, workerAntP1, warriorAntP2, workLifeTime, warLifeTime, workerAI, warriorAi);


        } catch (IOException | ClassNotFoundException e) {
            showAlert("Error loading config file: " + e.getMessage());
        }
    }

    /**
     * Сериализует список муравьёв в файл
     */
    public synchronized void saveAntsListToFile(File file) {
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(habitat.getCurrentTimeSimulation());
            oos.writeObject(habitat.getLoadTIme());
            oos.writeObject(SingletonDynamicArray.getInstance().getAntsList());
        } catch (IOException e) {
            showAlert("Error writing ants to file!");
        }
    }

    /**
     * Десериализует список муравьёв из файла и обновляет текущий список
     */
    public synchronized void loadAntsListFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            long time = (long) ois.readObject();
            loadedSimulationTime = (long) ois.readObject();

            ConcurrentLinkedQueue<Ant> ants = (ConcurrentLinkedQueue<Ant>) ois.readObject();
            SingletonDynamicArray.getInstance().setHabitat(habitat);
            isLoadedFromSave = true;

            for (Ant ant : ants) {
                long timeSinceSave = System.currentTimeMillis() - loadedSimulationTime;
                ant.setBirthTime(ant.getBirthTime() + timeSinceSave);
                habitat.restoreAntImageView(ant);
            }

            uiController.setTimeUntilLoadAnts(time);
            SingletonDynamicArray.getInstance().setAntsList(ants);
        } catch (IOException | ClassNotFoundException e) {
            showAlert("Error loading ants from file: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred.");
            alert.showAndWait();
        });
    }

    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }
    public void setUIController(UIController uiController) {
        this.uiController = uiController;
    }
}

