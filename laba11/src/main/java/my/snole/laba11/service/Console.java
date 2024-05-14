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
import my.snole.laba11.server.Message;
import my.snole.laba11.server.Server;

public class Console {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    private Stage stage;
    private Habitat habitat;
    String REQUEST_CLIENT_LIST = "request_client_list";
    String GET_OBJECTS = "get_objects";
    String SEND_OBJECTS = "send_objects";

    /**
     *  Контруктор с параметрами
     *  @param owner  Окно-владелец
     *  @param habitat Объект класса
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
     * Обрабатывает команды ввода пользователя и выполняет соответствующие действия
     *  Команды:
     * ? - connect <ip> <port>: подключает клиента к серверу по указанному IP и порту
     * ? - get ants <number> [port]: запрашивает указанное количество муравьёв
     * ? - start: запускает симуляцию
     * ? - stop: останавливает симуляцию
     */
    @FXML
    private void processCommand() {
        String command = textField.getText().trim();
        textField.clear();
        Platform.runLater(() -> {
            String[] parts = command.split(" ");
            switch (parts[0].toLowerCase()) {
                case "connect":
                    if (parts.length == 3) {
                        String ip = parts[1];
                        int port = Integer.parseInt(parts[2]);
                        boolean connected = habitat.client.connect(ip, port);
                        appendText(connected ? "Connected to server.\n" : "Failed to connect.\n");
                    } else {
                        appendText("Invalid command format. Use 'connect <ip> <port>'.\n");
                    }
                    break;
                case "get":
                    if (parts.length >= 2) {
                        switch (parts[1].toLowerCase()) {
                            case "list":
                                habitat.client.sendMessage(new Message(habitat.getClient().getId(), REQUEST_CLIENT_LIST, null, null, null, null, null));
                                appendText("Requested client list.\n");
                                break;
                            case "ants":
                                if (parts[1].equalsIgnoreCase("ants") && (parts.length == 4 || parts.length == 5)) {
                                    int numAnts = Integer.parseInt(parts[2]);
                                    int targetClientId = Integer.parseInt(parts[3]);
                                    habitat.client.sendMessage(new Message(habitat.getClient().getId(), GET_OBJECTS, null, null, numAnts, null, targetClientId));
                                    appendText("Requesting " + numAnts + " ants from client " + targetClientId + ".\n");
                                } else {
                                    appendText("Invalid command format. Use 'get ants <number> <targetClientId>'.\n");
                                }
                                break;
                            default:
                                appendText("Unknown get command: " + command + "\n");
                                break;
                        }
                    }
                    break;
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


//    @FXML
//    private void processCommand() {
//        String command = textField.getText().trim();
//        textField.clear();
//        Platform.runLater(() -> {
//            if (command.toLowerCase().startsWith("connect")) {
//                String[] parts = command.split(" ");
//                if (parts.length == 3) {
//                    String ip = parts[1];
//                    int port = Integer.parseInt(parts[2]);
//                    boolean connected = habitat.client.connect(ip, port);
//                    appendText(connected ? "Connected to server.\n" : "Failed to connect.\n");
//                }
//            } else if (command.equalsIgnoreCase("get list")) {
//                habitat.client.sendMessage(new Message(habitat.getClient().getId(), REQUEST_CLIENT_LIST, null, null, null, null));
//            }
//            else if (command.toLowerCase().startsWith("get ants")) {
//                String[] parts = command.split(" ");
//                if (parts.length == 3) {
//                    int numAnts = Integer.parseInt(parts[2]);
//                    habitat.client.sendMessage(new Message(habitat.getClient().getId(), GET_OBJECTS, null, null, numAnts, null));
//                }
//            }
//            else if (command.equalsIgnoreCase("start")) {
//                habitat.startSimulation();
//                appendText("Simulation started...\n");
//            } else if (command.equalsIgnoreCase("stop")) {
//                habitat.stopSimulation();
//                appendText("Simulation stopped...\n");
//            } else {
//                appendText("Unknown command: " + command + "\n");
//            }
//        });
//    }
