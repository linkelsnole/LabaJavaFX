package my.snole.laba11;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
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

import java.io.IOException;

public class AliveAntsDialog extends Dialog<Void> {
    @FXML
    private TableView<Ant> currentObjectsTable;

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
    }

    @FXML
    public void initialize() {
        TableColumn<Ant, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Ant, Long> lifeTimeCol = new TableColumn<>("Lifetime (ms)");
        lifeTimeCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getLifetime()).asObject());

        TableColumn<Ant, Long> birthTimeCol = new TableColumn<>("Birth time (ms)");
        birthTimeCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getBirthTime()).asObject());

        currentObjectsTable.getColumns().add(idCol);
        currentObjectsTable.getColumns().add(lifeTimeCol);
        currentObjectsTable.getColumns().add(birthTimeCol);

        startAutoUpdate();
    }

    public void updateTable() {
        ObservableList<Ant> ants = FXCollections.observableArrayList(SingletonDynamicArray.getInstance().getElements());
        System.out.println("Updating table with " + ants.size() + " ants");
        currentObjectsTable.setItems(ants);
    }
    public void startAutoUpdate() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            updateTable();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
