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

import java.io.IOException;

public class Console {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    private Stage stage;
    private Habitat habitat;

    /**
     *  Контруктор с параметрами
     *  @param owner  Окно-владелец
     *  @param habitat Объект класса [[Habitat]]
     */
    public Console(Stage owner, Habitat habitat) {
        this.habitat = habitat;
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
     * Обрабатывает команды ввода пользователя(start; stop) и выполняет соответствующие действия
     */
    @FXML
    private void processCommand() {
        String command = textField.getText().trim();
        textField.clear();
        Platform.runLater(() -> {
            switch (command.toLowerCase()) {
                case "start":
                    habitat.startSimulation();
                    appendText("Simulation started...\n");
                    break;
                case "stop":
                    habitat.stopSimulation();
                    appendText("Simulation stopped...\n");
                    break;
                default:
                    appendText("Unknown command: " + command + "\n");
                    break;
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

