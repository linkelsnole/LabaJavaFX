package my.snole.laba11.UIController;



import javafx.animation.PauseTransition;
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
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import my.snole.laba11.AliveAntsDialog;
import my.snole.laba11.Habitat;
import my.snole.laba11.HelloApplication;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.server.Message;
import my.snole.laba11.service.Config;
import my.snole.laba11.service.Console;
import my.snole.laba11.service.UIService;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;


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
    String REQUEST_CLIENT_LIST = "request_client_list";
    String GET_OBJECTS = "get_objects";
    String SEND_OBJECTS = "send_objects";

    /**
     * AI и приоритет
     */
    @FXML
    private ComboBox<Integer> workerAntPriorityComboBox;
    @FXML
    private ComboBox<Integer> warriorAntPriorityComboBox;
    @FXML
    public CheckBox warriorAI;
    @FXML
    public CheckBox workerAI;
    private BaseAI baseAI;
    Config config;

    float workerAntP1;
    float warriorAntP2;
    int workerAntN1;
    int warriorAntN2;
    long workLifeTime;
    long warLifeTime;

    @FXML
    private Button serverListButton;
    @FXML
    private Label idServerLabel;
    @FXML
    private ListView<String> clientListView;

    public void updateServerId() {
        Platform.runLater(() -> {
            idServerLabel.setText("ID: " + habitat.getId());
        });
    }

//    public void updateClientListView(List<String> clients) {
//        Platform.runLater(() -> {
//            System.out.println("метод updateClientListView вызывает");
//            // TODO: 11.05.2024 add timer task to update client list
////            habitat.getClient().sendMessage(new Message(habitat.getClient().getId(), REQUEST_CLIENT_LIST, null));
//            clientListView.getItems().setAll(clients);
//        });
//    }

    private Timer clientListUpdateTimer;
    public void updateClientListView(List<String> clients) {
        Platform.runLater(() -> {
            clientListView.getItems().setAll(clients);

            if (clientListUpdateTimer != null) {
                clientListUpdateTimer.cancel();
            }
            clientListUpdateTimer = new Timer();
            clientListUpdateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        habitat.getClient().sendMessage(new Message(habitat.getClient().getId(), REQUEST_CLIENT_LIST, null, null, null, null));
                    });
                }
            }, 5000, 5000);
        });
    }

    public void stopClientListUpdateTimer() {
        if (clientListUpdateTimer != null) {
            clientListUpdateTimer.cancel();
            clientListUpdateTimer = null;
        }
    }


    @FXML
    private void handleServerListButton() {
        if (habitat.getClient().isConnected()) {
            Message message = new Message(habitat.getClient().getId(), REQUEST_CLIENT_LIST, null, null, null, null);
            habitat.getClient().sendMessage(message);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText("Not Connected");
            alert.setContentText("Client is not connected to the server.");
            alert.showAndWait();
        }
    }

    private TimerTask createTimerTask() {
        float workerAntP1 = getProbability(comboProbWork, 70);
        float warriorAntP2 = getProbability(comboProbWar, 70);
        int workerAntN1 = parseInputOrUseDefault(timeTextWork, 2);
        int warriorAntN2 = parseInputOrUseDefault(timeTextWar, 2);
        long workLifeTime = parseInputOrUseDefault(lifeTimeTextWork, 10);
        long warLifeTime = parseInputOrUseDefault(lifeTimeTextWar, 12);
        boolean isWorkerAIChecked = workerAI.isSelected();
        boolean isWarriorAIChecked = warriorAI.isSelected();
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    habitat.update(System.currentTimeMillis(), workerAntN1, warriorAntN2, workerAntP1, warriorAntP2, workLifeTime, warLifeTime);
                    updateTimeLabel();
                });
            }
        };
    }

    @FXML
    private void initialize() {

        habitat = new Habitat(scenePane);
        habitat.setUIController(this);
        SingletonDynamicArray.getInstance().getConfig().setUIController(this);
        habitat.setSimulationStateListener(new Habitat.SimulationStateListener() {
            @Override
            public void onSimulationStarted() {
                Platform.runLater(() -> {
                    hidePopups();
                    scheduleTask();
                    startButton.setDisable(true);
                    stopButton.setDisable(false);
                    showInformationButton.setSelected(false);
                    hideInformationButton.setSelected(true);
                    habitat.toggleWorkerAntAI(workerAI.isSelected());
                    habitat.toggleWarriorAntAI(warriorAI.isSelected());
                    resetTransferredFlags();
                });
            }

            @Override
            public void onSimulationStopped() {
                Platform.runLater(() -> {
                    clearScene();
                    if (showWindow.isSelected() || showSummaryMenuItem.isSelected()) {
                        showStopSimulationDialog();
                    } else {
                        actuallyStopSimulation();
                    }
                    setButtonsStopped();
                    habitat.stopAnts();
                });
            }
        });

        initializeMenuBindings();
        setupListeners();
        updateServerId();
    }

    public void resetTransferredFlags() {
        SingletonDynamicArray.getInstance().getAntsList().stream()
                .filter(ant -> System.currentTimeMillis() > ant.getBirthTime() + ant.getLifetime())
                .forEach(ant -> ant.setTransferred(false));
    }
    private void setupListeners() {
        timeTextWork.textProperty().addListener((obs, oldVal, newVal) -> {
            workerAntN1 = parseInputOrUseDefault(timeTextWork, 2);
            SingletonDynamicArray.getInstance().getConfig().saveInFile();
        });
        timeTextWar.textProperty().addListener((obs, oldVal, newVal) -> {
            warriorAntN2 = parseInputOrUseDefault(timeTextWar, 2);
            SingletonDynamicArray.getInstance().getConfig().saveInFile();
        });
        lifeTimeTextWork.textProperty().addListener((obs, oldVal, newVal) -> {
            workLifeTime = parseInputOrUseDefault(lifeTimeTextWork, 10);
            SingletonDynamicArray.getInstance().getConfig().saveInFile();
        });
        lifeTimeTextWar.textProperty().addListener((obs, oldVal, newVal) -> {
            warLifeTime = parseInputOrUseDefault(lifeTimeTextWar, 12);
            SingletonDynamicArray.getInstance().getConfig().saveInFile();
        });
        comboProbWork.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) workerAntP1 = getProbability(comboProbWork, 80);
            SingletonDynamicArray.getInstance().getConfig().saveInFile();
        });
        comboProbWar.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) warriorAntP2 = getProbability(comboProbWar, 80);
            SingletonDynamicArray.getInstance().getConfig().saveInFile();
        });
//        workerAI.selectedProperty().addListener((obs, oldVal, isSelected) -> {
//            workerAI.isSelected();
//        });
//        warriorAI.selectedProperty().addListener((obs, oldVal, isSelected) -> {
//            warriorAI.isSelected();
//        });
    }


    private void updateTimeLabel() {
        if (!popup.getContent().isEmpty() && popup.getContent().get(0) instanceof Label) {
            Label label = (Label)  popup.getContent().get(0);
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
                        if (Habitat.isSimulationStopped) {
                            updateTimeLabel();
                        }
                        showTimePopup();
                        showInformationButton.setSelected(true);
                    } else {
                        popup.hide();
                        hideInformationButton.setSelected(true);
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
            if (Habitat.simulationActive) {
                updateTimeLabel();
            }
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
        return (float) Math.ceil(comboBox.getValue() * 0.01f * 100) / 100f;
    }


    private int parseInputOrUseDefault(TextField textField, int defaultValue) {
        String text = textField.getText();
        if (text.isEmpty()) {
            return defaultValue;
        }
        try {
            return service.parseInput(textField.getText());
        } catch (IllegalArgumentException e) {
            showErrorPopUp(defaultValue);
            textField.setText(String.valueOf(defaultValue));
            return defaultValue;
        }
    }

    private void initializeMenuBindings() {
        ToggleGroup timeToggleGroup = new ToggleGroup();
        showTimeMenuItem.setToggleGroup(timeToggleGroup);
        hideTimeMenuItem.setToggleGroup(timeToggleGroup);
        showTimeMenuItem.setSelected(false);
        showSummaryMenuItem.selectedProperty().bindBidirectional(showWindow.selectedProperty());
        showTimeMenuItem.selectedProperty().bindBidirectional(showInformationButton.selectedProperty());
        hideTimeMenuItem.selectedProperty().bindBidirectional(hideInformationButton.selectedProperty());
        lifeTimeTextWork.setText("10");
        lifeTimeTextWar.setText("10");
        timeTextWork.setText("2");
        timeTextWar.setText("2");
        comboProbWork.setValue(90);
        comboProbWar.setValue(90);
        workerAntPriorityComboBox.setValue(Thread.NORM_PRIORITY);
        warriorAntPriorityComboBox.setValue(Thread.NORM_PRIORITY);
        setupPriorityComboBox(workerAntPriorityComboBox, newVal -> habitat.changeWorkerAntPriority(newVal));
        setupPriorityComboBox(warriorAntPriorityComboBox, newVal -> habitat.changeWarriorAntPriority(newVal));

        workerAI.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (Habitat.simulationActive) {
                habitat.toggleWorkerAntAI(isSelected);
            }
        });

        warriorAI.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (Habitat.simulationActive) {
                habitat.toggleWarriorAntAI(isSelected);
            }
        });
    }

    private void setButtonsStopped() {
        showInformationButton.setSelected(false);
        hideInformationButton.setSelected(true);
        if (Habitat.simulationActive) {
            startButton.setDisable(true);
            stopButton.setDisable(false);
        } else {
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

    private void setupPriorityComboBox(ComboBox<Integer> comboBox, Consumer<Integer> changePriorityFunction) {
        comboBox.getItems().addAll(
                Thread.MIN_PRIORITY, Thread.NORM_PRIORITY, Thread.MAX_PRIORITY
        );

        comboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                changePriorityFunction.accept(newVal);
            }
        });
    }


    public void setSimulationParameters(long workerAntN1, long warriorAntN2, float workerAntP1, float warriorAntP2, long workLifeTime, long warLifeTime, boolean workerAI1, boolean warriorAI1) {
        Platform.runLater(() -> {
            timeTextWork.setText(String.valueOf(workerAntN1));
            timeTextWar.setText(String.valueOf(warriorAntN2));
            lifeTimeTextWork.setText(String.valueOf(workLifeTime));
            lifeTimeTextWar.setText(String.valueOf(warLifeTime));

            comboProbWar.getSelectionModel().select(Integer.valueOf((int) (warriorAntP2 * 100)));
            comboProbWork.getSelectionModel().select(Integer.valueOf((int) (workerAntP1 * 100)));


            workerAI.setSelected(workerAI1);
            warriorAI.setSelected(warriorAI1);
        });
    }

    public void setTimeUntilLoadAnts (long currentTimeSimulation) {
        habitat.setCurrentTimeSimulation(currentTimeSimulation);
        Habitat.startTime = currentTimeSimulation;
    }

    @FXML
    private void handleSaveSimulation(MouseEvent event) {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Habitat.simulationActive = false;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Simulation Ants");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data Files", "*.dat"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            SingletonDynamicArray.getInstance().getConfig().saveAntsListToFile(file);
        }
        Habitat.simulationActive = true;
    }

    @FXML
    private void handleLoadSimulation(MouseEvent event) {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Simulation Ants");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data Files", "*.dat"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (Habitat.simulationActive) {
            habitat.stopSimulation();
            PauseTransition pause = new PauseTransition(Duration.millis(50));
            pause.setOnFinished(e -> {
                if (file != null) {
                    SingletonDynamicArray.getInstance().getConfig().loadAntsListFromFile(file);
                }
            });
            pause.play();
        } else if (file != null) {
            SingletonDynamicArray.getInstance().getConfig().loadAntsListFromFile(file);
        }
    }

    @FXML
    private void handleOpenConsole(MouseEvent event) {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (HelloApplication.instance.console == null) {
            HelloApplication.instance.console = new Console(primaryStage, habitat);
        }
        HelloApplication.instance.console.show();
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


}