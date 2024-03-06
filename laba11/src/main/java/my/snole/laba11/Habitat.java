package my.snole.laba11;

import javafx.scene.control.*;
import my.snole.laba11.Ant.Ant;
import my.snole.laba11.Ant.WarriorAnt;
import my.snole.laba11.Ant.WorkerAnt;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;

import java.util.*;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import java.util.Optional;



public class Habitat  {
    private long startTime;
    private boolean isSimulationStopped = false;
    private long stoptime = 0;
    private int warriorAntcount = 0;
    private int workerAntcount = 0;
    List<Ant> list = new ArrayList<>();
    private WorkerAnt a = new WorkerAnt();
    private WarriorAnt b = new WarriorAnt();
    private Popup popup = new Popup();
    private Popup summaryPopup = new Popup();

    @FXML
    public AnchorPane scene;
    Timer timer = new Timer();
    TimerTask task;
    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    update(System.currentTimeMillis());
                    if (popup.isShowing()) {
                        updateTimeLabel();
                    }
                });
            }
        };
    }

    private void updateTimeLabel() {
        long timeFromStart = isSimulationStopped ? stoptime : System.currentTimeMillis() - startTime;
        long second = (timeFromStart / 1000) % 60;
        long minute = (timeFromStart / (1000 * 60)) % 60;
        long hour = (timeFromStart / (1000 * 60 * 60)) % 24;
        String timeString = String.format("%02d:%02d:%02d", hour, minute, second);
        if (!popup.getContent().isEmpty() && popup.getContent().get(0) instanceof Label) {
            Label label = (Label) popup.getContent().get(0);
            label.setText(timeString);
        }
    }



    private void showSummaryPopup() {
        long totalElapsedTime = isSimulationStopped ? stoptime : System.currentTimeMillis() - startTime;
        long second = (totalElapsedTime / 1000) % 60;
        long minute = (totalElapsedTime / (1000 * 60)) % 60;
        long hour = (totalElapsedTime / (1000 * 60 * 60)) % 24;

        Text timerText = new Text(String.format("Passed Time: %02d:%02d:%02d\n", hour, minute, second));
        timerText.setFill(Color.BLACK);
        timerText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 24));

        Text workerText = new Text(String.format("Worker Ants: %d\n", workerAntcount));
        workerText.setFill(Color.BLUE);
        workerText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 24));

        Text warriorText = new Text(String.format("Warrior Ants: %d", warriorAntcount));
        warriorText.setFill(Color.RED);
        warriorText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 24));

        TextFlow textFlow = new TextFlow(timerText, workerText, warriorText);
        textFlow.setStyle("-fx-background-color: #FFFFFF; " +
                "-fx-padding: 20px; " +
                "-fx-border-color: black; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");


        summaryPopup.getContent().add(textFlow);
        summaryPopup.show(scene.getScene().getWindow(),
                scene.getScene().getWindow().getX() + scene.getScene().getWidth() / 2 - textFlow.getBoundsInLocal().getWidth() / 2 - 20,
                scene.getScene().getWindow().getY() + scene.getScene().getHeight() / 2 - textFlow.getBoundsInLocal().getHeight() / 2);
    }

    private void showTimePopup() {
        Label label = new Label();
        label.setStyle("-fx-background-color: #d3d3d3; " +
                "-fx-text-fill: black; " +
                "-fx-padding: 10px; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: black; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius: 10px; " +
                "-fx-alignment: center; ");
        label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 18));
        label.setMinWidth(110);
        label.setMinHeight(40);
        if (popup.getContent().isEmpty()) {
            popup.getContent().add(label);
        }
        popup.show(scene.getScene().getWindow(),
                scene.getScene().getWindow().getX() + scene.getScene().getWidth() / 2 - label.getMinWidth() / 2,
                scene.getScene().getWindow().getY() + scene.getScene().getHeight() - label.getMinHeight() - 5);
    }
    private boolean eKeyPressed = false;
    public void run(KeyEvent event) {
        if (event.getCode() == KeyCode.B) {
            System.out.println("B key pressed");
            startSimulation();
        } else if (event.getCode() == KeyCode.E) {
            if (!eKeyPressed) {
                stopSimulation();
                showSummaryPopup();
            }
            System.out.println("E key pressed. Task cancelled");
        } else if (event.getCode() == KeyCode.T) {
            if (!summaryPopup.isShowing()) {
                if (!popup.isShowing()) {
                    updateTimeLabel();
                    showTimePopup();
                } else {
                    popup.hide();
                }
            }
            timer.purge();
            System.out.println("T pressed. Popup toggled.");
        }
        event.consume();
    }
    private void clearListAndTask() {
        list.clear();
        if (task != null) {
            task.cancel();
            task = null;
        }
        stoptime = System.currentTimeMillis() - startTime;
        timer.purge();
    }
    private void scheduleTask() {
        if (task != null) {
            task.cancel();
        }
        timer = new Timer();
        task = createTimerTask();
        timer.schedule(task, 0, 1000);
    }
    private void hidePopups () {
        if (summaryPopup.isShowing()) {
            summaryPopup.hide();
        }
        if (popup.isShowing()) {
            popup.hide();
        }
    }

    private void clearScene() {
        workerAntcount = 0;
        warriorAntcount = 0;


        if (popup.isShowing()) {
            popup.hide();
        }
        if (summaryPopup.isShowing()) {
            summaryPopup.hide();
        }

        scene.getChildren().removeIf(node -> node instanceof ImageView);
    }

    private void update(long time) {
        if (time %  a.getN1() == 0 && checkProbability(a.getP1())) {
            WorkerAnt workerAnt = new WorkerAnt();
            list.add(workerAnt);
            setAnt(workerAnt);
            System.out.println("workerAnt");
            workerAntcount++;
        }

        if (time % b.getN2() == 0 && checkProbability(b.getP2())) {
            WarriorAnt warriorAnt = new WarriorAnt();
            list.add(warriorAnt);
            setAnt(warriorAnt);
            System.out.println("warAnt");
            warriorAntcount++;
        }
    }

    private void setAnt(Ant ant) {
        int x = (int) (Math.random() * 700);
        int y = (int) (Math.random() * 550);
        ImageView imageView = new ImageView(ant.getImage());
        imageView.setX(x);
        imageView.setY(y);
        imageView.setVisible(true);
        scene.getChildren().add(imageView);
    }
    boolean checkProbability(float f) {
        float probability = (float)Math.random();
        return f <= probability;
    }





    //2 лаба---------------------------------
    @FXML
    private RadioButton showInformationButton;
    @FXML
    private RadioButton hideInformationButton;

    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private void handleStart(ActionEvent event) {
        startSimulation();
    }

    @FXML
    private void handleStop(ActionEvent event) {
        stopSimulation();
        showStopSimulationDialog();
    }
    @FXML
    private void handleShowInformationAction() {
        if (!popup.isShowing()) {
            updateTimeLabel();
            showTimePopup();
        }
    }
    @FXML
    private void handleHideInformationAction() {
        if (popup.isShowing()) {
            popup.hide();
        }
    }



    private void startSimulation() {
        clearScene();
        eKeyPressed = false;
        hidePopups();
        startTime = System.currentTimeMillis();
        scheduleTask();
        startButton.setDisable(true);
        stopButton.setDisable(false);
    }

    private void stopSimulation() {//!переделать чтобы была статистика по 7 пункту
        if (!eKeyPressed) {
            clearListAndTask();
            updateTimeLabel();
            popup.hide();
            eKeyPressed = true;
            startButton.setDisable(false);
            stopButton.setDisable(true);
        }
    }

    private void showStopSimulationDialog() {
        long totalElapsedTime = isSimulationStopped ? stoptime : System.currentTimeMillis() - startTime;
        long second = (totalElapsedTime / 1000) % 60;
        long minute = (totalElapsedTime / (1000 * 60)) % 60;
        long hour = (totalElapsedTime / (1000 * 60 * 60)) % 24;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(scene.getScene().getWindow());
        dialog.setTitle("Simulation information");
        dialog.setHeaderText("Simulation Summary:");

        //типы кнопок
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextArea textArea = new TextArea();//отображение инфы
        textArea.setEditable(false);
        textArea.setText(String.format("Passed Time: %02d:%02d:%02d\nWorker Ants: %d\nWarrior Ants: %d", hour, minute, second, workerAntcount, warriorAntcount));
        dialog.getDialogPane().setContent(textArea);


        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            stopSimulation();
        } else {

        }
    }

}