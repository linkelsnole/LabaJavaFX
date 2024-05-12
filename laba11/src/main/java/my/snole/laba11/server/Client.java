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
import my.snole.laba11.model.ant.WarriorAnt;
import my.snole.laba11.model.ant.WorkerAnt;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Client {
    private int id;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private boolean connected = false;
    private Thread thread;
    private Habitat habitat;
    SingletonDynamicArray singletonDynamicArray;
    String REQUEST_CLIENT_LIST = "request_client_list";
    String GET_OBJECTS = "get_objects";
    String SEND_OBJECTS = "send_objects";
    String CLIENT_LIST = "client_list";



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
                    String messageString = (String) obj;
                    System.out.println("Message received: " + messageString);
                    ObjectMapper objectMapper = new ObjectMapper();
                    Message message = objectMapper.readValue(messageString, Message.class);
//                    if (CLIENT_LIST.equals(message.getMethod())) {
//                        showClientListDialog(message.getClientListString());//для ServerListButton
//
//                    }
                    handleServerMessage(message);
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

    private void handleServerMessage(Message message) {
        System.out.println("handle server message: " + message.getMethod() + ", from " + message.getSender());
        switch (message.getMethod()) {
            case "client_list":
                if (message.getClientListString() != null) {
                    String[] parts = message.getClientListString().split(",");
                    List<String> clientDetails = Arrays.asList(parts);
                    habitat.updateClientListView(clientDetails);
                }
                break;
            case "get_objects":
                requestAnts(message.getTransferObjectCount());
                break;
            case "send_objects":
                receiveAnts(message.getAnts());
                break;
        }
    }

    private void requestAnts(int numberOfAnts) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Ant> list = new ArrayList<>(SingletonDynamicArray.getInstance().getAntsList());
            List<TransferObject> transferObjects;
            //если список муравьёв меньше, чем запрошенное кол-во, отправляем все что есть в симуляции
            if (list.size() <= numberOfAnts) {
                transferObjects = list.stream().map(this::antToTransferObject).collect(Collectors.toList());
                list.clear();
                // TODO: 11.05.2024 remove ants from visual panel
            } else {
                //выбираем случайные элементы
                Random random = new Random();
                List<Ant> selectedAnts = random.ints(0, list.size()).distinct().limit(numberOfAnts)
                        .mapToObj(list::get).collect(Collectors.toList());
                list.removeAll(selectedAnts);
                transferObjects = selectedAnts.stream().map(this::antToTransferObject).collect(Collectors.toList());
                // TODO: 11.05.2024 remove ants from visual panel
            }
            Message message = new Message(getId(), SEND_OBJECTS, null, null, transferObjects.size(), transferObjects);
            out.writeObject(objectMapper.writeValueAsString(message));

        } catch (IOException e) {
            Platform.runLater(() -> System.out.println("Failed to send ants: " + e.getMessage()));
        }
    }

    private void receiveAnts(List<TransferObject> transferObjects) {
        if (!transferObjects.isEmpty()) {
            Platform.runLater(() -> {
                try {
                    List<Ant> ants = transferObjects.stream().map(this::transferObjectToAnt).collect(Collectors.toList());
                    for (Ant ant : ants) {
                        habitat.setAnt(ant);
                        SingletonDynamicArray.getInstance().addElement(ant, ant.getBirthTime());
                        habitat.restoreAntImageView(ant);
                    }
                } catch (Exception e) {
                    System.out.println("Error receiving ants: " + e.getMessage());
                }
            });
        }
    }

    public void sendMessage(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (connected) {
            try {
                out.writeObject(objectMapper.writeValueAsString(message));
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

    private TransferObject antToTransferObject(Ant ant){
        TransferObject transferObject = new TransferObject();
        transferObject.setAntType(ant instanceof WarriorAnt ? AntType.WARRIOR : AntType.WORKER);
        transferObject.setBirthTime(ant.getBirthTime());
        transferObject.setLifetime(ant.getLifetime());
        return transferObject;
    }

    private Ant transferObjectToAnt(TransferObject transferObject){
        Ant ant = (transferObject.getAntType().equals(AntType.WARRIOR) ? new WarriorAnt() : new WorkerAnt());
        ant.setLifetime(transferObject.getLifetime());
        ant.setBirthTime(transferObject.getBirthTime());
        return ant;
    }
}

