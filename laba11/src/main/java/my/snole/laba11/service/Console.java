package my.snole.laba11.service;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import my.snole.laba11.Habitat;
import my.snole.laba11.server.Client;

import java.io.IOException;

public class Console {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    private Stage stage;
    private Habitat habitat;
    private Client client;

    /**
     *  Контруктор с параметрами
     *  @param owner  Окно-владелец
     *  @param habitat Объект класса [[Habitat]]
     */
    public Console(Stage owner, Habitat habitat) {
        this.habitat = habitat;
//        this.client = new Client(habitat);
        FXMLLoader loader = new FXMLLoader(Console.class.getResource("/my/snole/laba11/console.fxml"));
        loader.setController(this);
        try {
            Parent root = loader.load();
            stage = new Stage();
            stage.initOwner(owner);
            stage.setTitle("Console");
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обрабатывает команды ввода пользователя и выполняет соответствующие действия
     */
    @FXML
    private void processCommand() {
        String command = textField.getText().trim();
        textField.clear();
        Platform.runLater(() -> {
            if (command.toLowerCase().startsWith("connect")) {
                String[] parts = command.split(" ");
                if (parts.length == 3) {
                    String ip = parts[1];
                    int port = Integer.parseInt(parts[2]);
                    boolean connected = habitat.client.connect(ip, port);
                    appendText(connected ? "Connected to server.\n" : "Failed to connect.\n");
                }
            } else if (command.equalsIgnoreCase("get list")) {
                habitat.client.sendMessage("list");
            } else if (command.toLowerCase().startsWith("get ants")) {
                String[] parts = command.split(" ");
                if (parts.length == 3) {
                    int numAnts = Integer.parseInt(parts[2]);
                    habitat.client.sendMessage("giveObject " + numAnts);
                }
            } else if (command.equalsIgnoreCase("start")) {
                habitat.startSimulation();
                appendText("Simulation started...\n");
            } else if (command.equalsIgnoreCase("stop")) {
                habitat.stopSimulation();
                appendText("Simulation stopped...\n");
            } else {
                appendText("Unknown command: " + command + "\n");
            }
        });
    }

    public void show() {
        if (stage != null) {
            stage.show();
        }
    }

    private void appendText(String text) {
        textArea.appendText(text);
    }
}

