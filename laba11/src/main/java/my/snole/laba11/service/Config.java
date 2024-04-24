package my.snole.laba11.service;

import java.io.Serializable;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import my.snole.laba11.Habitat;
import my.snole.laba11.UIController.UIController;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.ant.Ant;

public class Config implements Serializable {
    private Habitat habitat;
    private UIController uiController;

    public synchronized void saveInFile() {
        try (FileOutputStream fileOutputStream = new FileOutputStream("config.txt");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

            objectOutputStream.writeObject(habitat.getCurrentTimeSimulation());
            objectOutputStream.writeObject(habitat.getWorkerAntN1());
            objectOutputStream.writeObject(habitat.getWarriorAntN2());
            objectOutputStream.writeObject(habitat.getWorkerAntP1());
            objectOutputStream.writeObject(habitat.getWarriorAntP2());
            objectOutputStream.writeObject(habitat.getWorkLifeTime());
            objectOutputStream.writeObject(habitat.getWarLifeTime());
            objectOutputStream.writeObject(SingletonDynamicArray.getInstance().getAntsList());
        } catch (FileNotFoundException e) {
            showAlert("Error! Config file not found!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Сохранение прошло с ошибкой IOException");
            showAlert("Error writing to config file!");
        }
    }


    public synchronized void loadFromFile() {
        try (FileInputStream fileInputStream = new FileInputStream("config.txt");
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            long time = (long) objectInputStream.readObject();
            long workerAntN1 = (long) objectInputStream.readObject();
            long warriorAntN2 = (long) objectInputStream.readObject();
            float workerAntP1 = (float) objectInputStream.readObject();
            float warriorAntP2 = (float) objectInputStream.readObject();
            long workLifeTime = (long) objectInputStream.readObject();
            long warLifeTime = (long) objectInputStream.readObject();
            ConcurrentLinkedQueue<Ant> ants = (ConcurrentLinkedQueue<Ant>) objectInputStream.readObject();

            uiController.setSimulationParameters(time, workerAntN1, warriorAntN2, workerAntP1, warriorAntP2, workLifeTime, warLifeTime);

            SingletonDynamicArray.getInstance().setAntsList(new ConcurrentLinkedQueue<>());

            SingletonDynamicArray.getInstance().setHabitat(habitat);
            for (Ant ant : ants) {
                habitat.restoreAntImageView(ant);
            }

        } catch (IOException | ClassNotFoundException e) {

            showAlert("Error loading config file: " + e.getMessage());
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }
    public void setUIController(UIController uiController) {
        this.uiController = uiController;
    }
}

