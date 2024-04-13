package my.snole.laba11.UIController;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;
import my.snole.laba11.AliveAntsDialog;
import my.snole.laba11.Habitat;
import my.snole.laba11.HelloApplication;
import my.snole.laba11.service.UIService;
import java.util.*;


public class UIController {
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
    @FXML
    private RadioMenuItem showTimeMenuItem;
    @FXML
    private RadioMenuItem hideTimeMenuItem;
    @FXML
    private CheckMenuItem showSummaryMenuItem;
    Timer timer = new Timer();
    TimerTask task;
    @FXML
    private TextField lifeTimeTextWork;
    @FXML
    private TextField lifeTimeTextWar;
    @FXML
    private Button curObjBtn;
    @FXML
    private Pane scenePane;//окно где появляются муравьи

    /**
     * AI и приоритет
     */
    @FXML
    private ComboBox<Integer> workerAntPriorityComboBox;
    @FXML
    private ComboBox<Integer> warriorAntPriorityComboBox;
    @FXML
    private CheckBox warriorAI;
    @FXML
    private CheckBox workerAI;







    private TimerTask createTimerTask() {
        float workerAntP1 = getProbability(comboProbWork, 40);
        float warriorAntP2 = getProbability(comboProbWar, 30);
        int workerAntN1 = parseInputOrUseDefault(timeTextWork, 3);
        int warriorAntN2 = parseInputOrUseDefault(timeTextWar, 5);
        long workLifeTime = parseInputOrUseDefault(lifeTimeTextWork, 10);
        long warLifeTime = parseInputOrUseDefault(lifeTimeTextWar, 12);
        boolean isWorkerAIChecked = workerAI.isSelected();
        boolean isWarriorAIChecked = warriorAI.isSelected();
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    habitat.update(System.currentTimeMillis(), workerAntN1, warriorAntN2, workerAntP1, warriorAntP2, workLifeTime, warLifeTime, isWorkerAIChecked, isWarriorAIChecked);
                    if (popup.isShowing()) {
                        updateTimeLabel();
                    }
                });
            }
        };
    }
    @FXML
    private void initialize() {

        habitat = new Habitat(scenePane);
        habitat.setSimulationStateListener(new Habitat.SimulationStateListener() {
            @Override
            public void onSimulationStarted() {
                Platform.runLater(() -> {
                    clearScene();
                    hidePopups();
                    scheduleTask();
                    showInformationButton.setSelected(false);
                    hideInformationButton.setSelected(true);
                    startButton.setDisable(true);
                    stopButton.setDisable(false);
                });
            }
            @Override
            public void onSimulationStopped() {
                Platform.runLater(() -> {
                    if (showWindow.isSelected() || showSummaryMenuItem.isSelected()) {
                        showStopSimulationDialog();
                    } else {
                        actuallyStopSimulation();
                    }
                    showInformationButton.setSelected(false);
                    hideInformationButton.setSelected(true);
                    setButtonsStopped();

                });
            }
        });
        lifeTimeTextWork.setText("3");
        lifeTimeTextWar.setText("5");
        timeTextWork.setText("3");
        timeTextWar.setText("3");
        comboProbWork.setValue(50);
        comboProbWar.setValue(50);
        initializeMenuBindings();

        //инициализация ComboBox для выбора приоритета потоков
        workerAntPriorityComboBox.getItems().addAll(
                Thread.MIN_PRIORITY, Thread.NORM_PRIORITY, Thread.MAX_PRIORITY
        );
        warriorAntPriorityComboBox.getItems().addAll(
                Thread.MIN_PRIORITY, Thread.NORM_PRIORITY, Thread.MAX_PRIORITY
        );

        //слушателт для ComboBox
        workerAntPriorityComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                habitat.changeWorkerAntPriority(newVal);
            }
        });

        warriorAntPriorityComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                habitat.changeWarriorAntPriority(newVal);
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

    public void run(KeyEvent event) {
        switch (event.getCode()) {
            case B:
                System.out.println("B key pressed");
                habitat.startSimulation();
                break;
            case E:
                if (!Habitat.eKeyPressed) {
                    habitat.stopSimulation();
                    showSummaryPopup();
                }
                System.out.println("E key pressed. Task cancelled");
                break;
            case T:
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
                break;
            default:
                break;
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

    // Окно статистики
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
            popup.hide();
        }
    }

    //Окно ошибки
    void showErrorPopUp(int defaultValue) {
        Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Data entry error");
                    alert.setHeaderText("Incorrect value entered! Simulation is running using the default value (" + defaultValue + ")");
                    alert.showAndWait();
                }
        );
    }

    // Вспомогательные методы
    public void actuallyStopSimulation() {
        clearListAndTask();
        updateTimeLabel();
        popup.hide();
        Habitat.simulationActive = false;
    }


    private void clearListAndTask() {
        Habitat.list.clear();
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

        scenePane.getChildren().removeIf(node -> node instanceof ImageView);
    }

    private float getProbability(ComboBox<Integer> comboBox, float defaultValue) {
        if (comboBox.getValue() == null) {
            return defaultValue;
        }
        return comboBox.getValue() * 0.01f;
    }

    private int parseInputOrUseDefault(TextField textField, int defaultValue) {
        try {
            return service.parseInput(textField.getText());
        } catch (IllegalArgumentException e) {
            showErrorPopUp(defaultValue);
            textField.setText(String.valueOf(defaultValue));
            return defaultValue;
        }
    }

    private void initializeMenuBindings () {
        ToggleGroup timeToggleGroup = new ToggleGroup();
        showTimeMenuItem.setToggleGroup(timeToggleGroup);
        hideTimeMenuItem.setToggleGroup(timeToggleGroup);
        showTimeMenuItem.setSelected(false);
        showSummaryMenuItem.selectedProperty().bindBidirectional(showWindow.selectedProperty());
        showTimeMenuItem.selectedProperty().bindBidirectional(showInformationButton.selectedProperty());
        hideTimeMenuItem.selectedProperty().bindBidirectional(hideInformationButton.selectedProperty());
    }

    private void setButtonsStopped () {
        showInformationButton.setSelected(false);
        hideInformationButton.setSelected(true);
        if(Habitat.simulationActive) {
            startButton.setDisable(true);
            stopButton.setDisable(false);
        }
        else {
            startButton.setDisable(false);
            stopButton.setDisable(true);
        }
    }
    @FXML
    private void handleShowSummaryAction() {
    }


    @FXML
    private void showCurrentObjectsDialog(MouseEvent event) {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (HelloApplication.instance.aliveAntsDialog == null) {
            HelloApplication.instance.aliveAntsDialog = new AliveAntsDialog(primaryStage);
        }
        HelloApplication.instance.aliveAntsDialog.show();
    }


}