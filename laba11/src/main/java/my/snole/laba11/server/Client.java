package my.snole.laba11.server;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.Habitat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Client {
    private int id;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private boolean connected = false;
    private Thread thread;
    private Habitat habitat;
    SingletonDynamicArray singletonDynamicArray;



    public Client(Habitat habitat) {
        this.habitat = habitat;
        this.id = SingletonDynamicArray.getInstance().generateUniqueId();
    }

    public synchronized boolean connect(String ip, int port) {
        if (!connected) {
            try {
                socket = new Socket(ip, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                connected = true;

                thread = new Thread(() -> {
                    try {
                        out.writeObject("id:" + id);
                        processMessages();
                    } catch (IOException e) {
                        disconnect();
                    }
                });
                thread.start();
                return true;
            } catch (IOException e) {
                disconnect();
            }
        }
        return false;
    }

    private void processMessages() {
        try {
            while (connected) {
                Object obj = in.readObject();
                if (obj instanceof String) {
                    String message = (String) obj;
                    System.out.println("Message received: " + message);
                    if (message.startsWith("Client List:")) {
                        showClientListDialog(message.substring(12));//для ServerListButton
                        handleServerMessage(message);
                    } else {
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            Platform.runLater(() -> System.out.println("Connection error: " + e.getMessage()));
            disconnect();
        }
    }

    private void showClientListDialog(String clientList) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connected Clients");
            alert.setHeaderText(null);
            alert.setContentText(clientList);
            alert.showAndWait();
        });
    }

    private void handleServerMessage(String message) {
        String[] parts = message.split(":", 2);
        switch (parts[0].trim()) {
            case "Client List":
                List<String> clientDetails = Arrays.asList(parts[1].split(","));
                habitat.updateClientListView(clientDetails);
                System.out.println("Method Comppppl");
                break;
            case "giveObject":
                requestAnts(Integer.parseInt(parts[1]));
                break;
            case "getObject":
                receiveAnts(parts[1]);
                break;
        }
    }

    private void requestAnts(int numberOfAnts) {
        try {
            List<Ant> list = new ArrayList<>(SingletonDynamicArray.getInstance().getAntsList());

            //если список муравьёв меньше, чем запрошенное кол-во, отправляем все что есть в симуляции
            if (list.size() <= numberOfAnts) {
                out.writeObject(new ArrayList<>(list));
            } else {
                //выбираем случайные элементы
                Random random = new Random();
                List<Ant> selectedAnts = random.ints(0, list.size()).distinct().limit(numberOfAnts)
                        .mapToObj(list::get).collect(Collectors.toList());
                out.writeObject(selectedAnts);
            }
        } catch (IOException e) {
            Platform.runLater(() -> System.out.println("Failed to send ants: " + e.getMessage()));
        }
    }

    private void receiveAnts(String data) {
        if (!"null".equals(data)) {
            Platform.runLater(() -> {
                try {
                    ArrayList<Ant> ants = (ArrayList<Ant>) in.readObject();
                    for (Ant ant : ants) {
                        SingletonDynamicArray.getInstance().addElement(ant, ant.getBirthTime());
                        habitat.restoreAntImageView(ant);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error receiving ants: " + e.getMessage());
                }
            });
        }
    }

    public void sendMessage(String message) {
        if (connected) {
            try {
                out.writeObject(message);
            } catch (IOException e) {
                Platform.runLater(() -> System.out.println("Error sending message: " + e.getMessage()));
                disconnect();
            }
        }
    }

    public void disconnect() {
        if (connected) {
            try {
                connected = false;
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
                Platform.runLater(() -> {
                    habitat.updateClientListView(new ArrayList<>());
                    System.out.println("Client list cleared");
                });
            } catch (IOException e) {
                Platform.runLater(() -> System.out.println("Error closing connection: " + e.getMessage()));
            }
        }
    }
    public int getId() {
        return id;
    }
    public boolean isConnected() {
        return connected;
    }
}

