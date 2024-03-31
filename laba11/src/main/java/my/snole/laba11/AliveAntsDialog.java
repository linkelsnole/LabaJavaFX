package my.snole.laba11;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import my.snole.laba11.model.Ant.Ant;
import my.snole.laba11.model.SingletonDynamicArray;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.io.IOException;

public class AliveAntsDialog extends Dialog<Void> {
    @FXML
    public TableView<Ant> currentObjectsTable;
    @FXML
    private DialogPane dialogPane;
    public Timeline timeline;

    public AliveAntsDialog() {
        super();


    }

    public AliveAntsDialog(Window owner) {
        super();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AliveAntsDialog.class.getResource("aliveAnts.fxml"));
            initOwner(owner);
            setTitle("Alive Ants");

            DialogPane dialogPane = loader.load();
            setDialogPane(dialogPane);

            Stage stage = (Stage) getDialogPane().getScene().getWindow();
            stage.setOnCloseRequest(event -> close());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setOnShown(event -> {
            startAutoUpdate();
        });
        this.setOnHidden(event -> {
            stopAutoUpdate();
        });

    }

    @FXML
    public void initialize() {
        TableColumn<Ant, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Ant, Long> lifeTimeCol = new TableColumn<>("Lifetime (s)");
        lifeTimeCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getLifetime()).asObject());

        TableColumn<Ant, String> birthTimeCol = new TableColumn<>("Birth time(s)");
        birthTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(formatBirthTime(cellData.getValue().getBirthTime())));


        currentObjectsTable.getColumns().add(idCol);
        currentObjectsTable.getColumns().add(lifeTimeCol);
        currentObjectsTable.getColumns().add(birthTimeCol);
        System.out.println("TableView is " + (currentObjectsTable != null ? "initialized" : "null"));
        startAutoUpdate();

    }

    public void updateTable() {
        if (currentObjectsTable != null) {
            ObservableList<Ant> ants = FXCollections.observableArrayList(SingletonDynamicArray.getInstance().getElements());
            currentObjectsTable.setItems(ants);
        }
    }
    public void startAutoUpdate() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> Platform.runLater(this::updateTable)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void stopAutoUpdate() {
        if (timeline != null) {
            timeline.stop();
        }
    }










    public String formatBirthTime(long birthTimeMillis) {
        long timeFromStart = birthTimeMillis - Habitat.startTime;
        int seconds = (int)(timeFromStart / 1000);
        String formatted = seconds < 10 ? String.valueOf(seconds) : String.format("%02d", seconds);
        return seconds < 0 ? "-" + formatted : formatted;
    }
}
