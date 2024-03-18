package my.snole.laba11.UIController;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import my.snole.laba11.Habitat;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.service.UIService;
import java.util.*;


public class UIController {
//    private long startTime;
//    private final boolean isSimulationStopped = false;
//    private long stoptime = 0;
//    private int warriorAntcount = 0;
//    private int workerAntcount = 0;
    private SingletonDynamicArray list;
    private final Popup popup = new Popup();
    private final Popup summaryPopup = new Popup();
    private final UIService service = new UIService();

    @FXML
    public AnchorPane scene;
    private Habitat habitat;

    @FXML
    private ComboBox<Integer> comboProbWork;
    @FXML
    private ComboBox<Integer> comboProbWar;
    @FXML
    private TextField timeTextWork;
    @FXML
    private TextField timeTextWar;
    @FXML
    private RadioButton showInformationButton;
    @FXML
    private RadioButton hideInformationButton;

    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private CheckBox showWindow;


    Timer timer = new Timer();
    TimerTask task;

    private TimerTask createTimerTask() {
        float workerAntP1 = (comboProbWork.getValue() == null ? 40 : comboProbWork.getValue()) * 0.01f;
        float warriorAntP2 = (comboProbWar.getValue() == null ? 30 : comboProbWar.getValue()) * 0.01f;
        int workerAntN1;
        int warriorAntN2;
        try {
            workerAntN1 = service.parseInput(timeTextWork.getText());
        } catch (IllegalArgumentException e) {
            showErrorPopUp(3);
            workerAntN1 = 3;
        }
        try {
            warriorAntN2 = service.parseInput(timeTextWar.getText());
        } catch (IllegalArgumentException e) {
            showErrorPopUp(5);
            warriorAntN2 = 5;
        }
        int finalWorkerAntN = workerAntN1;
        int finalWarriorAntN = warriorAntN2;
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    habitat.update(System.currentTimeMillis(), finalWorkerAntN, finalWarriorAntN, workerAntP1, warriorAntP2);
                    if (popup.isShowing()) {
                        updateTimeLabel();
                    }
                });
            }
        };
    }
    @FXML
    private void initialize() {
        habitat = new Habitat(scene);
        habitat.setHabitatListener(imageView -> scene.getChildren().add(imageView));
        habitat.setSimulationStateListener(new Habitat.SimulationStateListener() {
            @Override
            public void onSimulationStarted() {
                Platform.runLater(() -> {
                    clearScene();
                    hidePopups();
                    scheduleTask();
                    startButton.setDisable(true);
                    stopButton.setDisable(false);
                });
            }
            @Override
            public void onSimulationStopped() {
                Platform.runLater(() -> {
                    if (showWindow.isSelected()) {
                        showStopSimulationDialog();
                    } else {
                        actuallyStopSimulation();
                    }
                    startButton.setDisable(false);
                    stopButton.setDisable(true);
                });
            }
        });
    }


    private void updateTimeLabel() {
        if (!popup.getContent().isEmpty() && popup.getContent().get(0) instanceof Label label) {
            label.setText(service.generateTimeString(Habitat.isSimulationStopped, Habitat.stoptime, Habitat.startTime));
        }
    }

    private void showSummaryPopup() {
        Text timerText = new Text(String.format("Passed Time: %s\n", service.generateTimeString(Habitat.isSimulationStopped, Habitat.stoptime, Habitat.startTime)));
        timerText.setFill(Color.BLACK);
        timerText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 24));

        Text workerText = new Text(String.format("Worker Ants: %d\n", Habitat.workerAntcount));
        workerText.setFill(Color.BLUE);
        workerText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 24));

        Text warriorText = new Text(String.format("Warrior Ants: %d", Habitat.warriorAntcount));
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

    public void run(KeyEvent event) {//сделать switch
        if (event.getCode() == KeyCode.B) {
            System.out.println("B key pressed");
            habitat.startSimulation();
        } else if (event.getCode() == KeyCode.E) {
            if (!Habitat.eKeyPressed) {
                habitat.stopSimulation();
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



    @FXML
    private void handleStart() {
        habitat.startSimulation();
    }

    @FXML
    private void handleStop() {
        habitat.stopSimulation();
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





    private void showStopSimulationDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(scene.getScene().getWindow());
        dialog.setTitle("Simulation information");
        dialog.setHeaderText("Simulation Summary:");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setText(String.format("Passed Time: %s\nWorker Ants: %d\nWarrior Ants: %d", service.generateTimeString(Habitat.isSimulationStopped, Habitat.stoptime, Habitat.startTime), Habitat.workerAntcount, Habitat.warriorAntcount));
        dialog.getDialogPane().setContent(textArea);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            actuallyStopSimulation();
        } else {
            Habitat.eKeyPressed = false;
            Habitat.simulationActive = true;
        }
    }

    void showErrorPopUp(int defaultValue) {
        Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка ввода данных");
                    alert.setHeaderText("Введено неверное значение! Симуляция выполняется с использованием значения по умолчанию (" + defaultValue + ")");
                    alert.showAndWait();
                }
        );
    }

    public void actuallyStopSimulation() {//перенести
        clearListAndTask();
        updateTimeLabel();
        popup.hide();
        Habitat.simulationActive = false;
    }


    private void clearListAndTask() {
        list.clear();
        if (task != null) {
            task.cancel();
            task = null;
        }
        Habitat.stoptime = System.currentTimeMillis() - Habitat.startTime;
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

    private void hidePopups() {
        if (summaryPopup.isShowing()) {
            summaryPopup.hide();
        }
        if (popup.isShowing()) {
            popup.hide();
        }
    }

    private void clearScene() {
        Habitat.workerAntcount = 0;
        Habitat.warriorAntcount = 0;

        if (popup.isShowing()) {
            popup.hide();
        }
        if (summaryPopup.isShowing()) {
            summaryPopup.hide();
        }

        scene.getChildren().removeIf(node -> node instanceof ImageView);
    }

}
