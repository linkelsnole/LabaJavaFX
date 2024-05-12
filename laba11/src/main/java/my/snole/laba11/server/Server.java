package my.snole.laba11.server;


import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Collections;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import my.snole.laba11.Habitat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.model.ant.WarriorAnt;


public class Server {
    @FXML
    private TextArea textArea;
    @FXML
    private Button buttonOn;
    @FXML
    private Button buttonOff;
    @FXML
    private Button buttonList;
    @FXML
    private Button buttonPort;

    private ServerSocket serverSocket;
    private Thread thread;
    private ArrayList<ServerClient> clients = new ArrayList<>();
    private boolean serverRunning = false;
    String REQUEST_CLIENT_LIST = "request_client_list";
    String GET_OBJECTS = "get_objects";
    String SEND_OBJECTS = "send_objects";
    String CLIENT_LIST = "client_list";

    private Client client;

    private int port;


    @FXML
    private void handleButtonOn() {
        if (!serverRunning) {
            serverRunning = true;
            startServer();
            textArea.appendText("Server started on port " + port + "\n");
            buttonOff.setDisable(false);
            buttonList.setDisable(false);
            buttonOn.setDisable(true);
            buttonPort.setDisable(true);
        }
    }

    @FXML
    private void handleButtonOff() {
        if (serverRunning) {
            stopServer();
            textArea.appendText("Server stopped.\n");
            buttonOff.setDisable(true);
            buttonList.setDisable(true);
            buttonOn.setDisable(false);
            buttonPort.setDisable(false);
        }
    }

    @FXML
    private void handleButtonList() {
        Platform.runLater(() -> {
            textArea.appendText("Connected clients:\n");
            for (ServerClient client : clients) {
                textArea.appendText("Client ID: " + client.id + "\n");
            }
        });
    }

    public void showListServer () {

    }

    @FXML
    private void handleButtonPort() {
        TextInputDialog dialog = new TextInputDialog("4000");
        dialog.setTitle("Set Server Port");
        dialog.setHeaderText("Change Server Port");
        dialog.setContentText("Please enter a port number:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(portNumber -> {
            try {
                port = Integer.parseInt(portNumber);
                textArea.appendText("Port set to " + port + ". Ready to launch the server.\n");
                buttonOn.setDisable(false);
            } catch (NumberFormatException e) {
                textArea.appendText("Invalid port number. Please enter a valid number.\n");
            }
        });
    }

    private synchronized void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            thread = new Thread(() -> {
                try {
                    while (serverRunning) {
                        Socket socket = serverSocket.accept();
                        ServerClient client = new ServerClient(socket);
                        Platform.runLater(() -> textArea.appendText("New client connected: " + socket + "\n"));
                        clients.add(client);
                        new Thread(client).start();
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> textArea.appendText("Error accepting client connection: " + e.getMessage() + "\n"));
                }
            });
            thread.start();
        } catch (IOException e) {
            Platform.runLater(() -> textArea.appendText("Could not start server on port " + port + ": " + e.getMessage() + "\n"));
            serverRunning = false;
        }
    }
    private void handleClientMessage(String message, ServerClient client) {
        if (message.equals("request_client_list")) {
            sendClientList();
        }
    }

//    private void sendClientList() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        for (ServerClient client : clients) {
//            try {
//                Message message = new Message();
//                message.setMethod(CLIENT_LIST);
//                message.setClientListString(clients.stream()
//                        .map(serverClient -> "Client ID: " + serverClient.getId() + " -> Port: " + serverClient.socket.getPort())
//                        .collect(Collectors.joining("\n")));// ! заменил на порт сервера
//                client.out.writeObject(objectMapper.writeValueAsString(message));
//            } catch (IOException e) {
//                System.out.println("Failed to send client list to: " + client.getId());
//                e.printStackTrace();
//            }
//        }
//    }

    private synchronized void sendClientList() {
        ObjectMapper objectMapper = new ObjectMapper();
        String clientListString = clients.stream()
                .map(client -> "Client ID: " + client.getId() + " -> Port: " + port)
                .collect(Collectors.joining("\n"));

        List<ServerClient> clientsSnapshot = new ArrayList<>(clients);
        for (ServerClient client : clientsSnapshot) {
            try {
                Message message = new Message();
                message.setMethod(CLIENT_LIST);
                message.setClientListString(clientListString);
                client.out.writeObject(objectMapper.writeValueAsString(message));
            } catch (IOException e) {
                System.out.println("Failed to send client list to: " + client.getId());
                e.printStackTrace();
                client.close();
            }
        }
    }

    private void stopServer() {
        try {
            serverRunning = false;
            for (ServerClient client : clients) {
                client.close();
            }
            clients.clear();
            serverSocket.close();
            thread.interrupt();
            sendClientListUpdate(new ArrayList<>());
        } catch (IOException e) {
            Platform.runLater(() -> textArea.appendText("Error stopping server: " + e.getMessage() + "\n"));
        }
    }
    /**
     * Метод для отправки пустого списка
     */
    private void sendClientListUpdate(List<String> clientList) {
        ObjectMapper objectMapper = new ObjectMapper();
        for (ServerClient client : clients) {
            try {
                Message message = new Message();
                message.setMethod(CLIENT_LIST);
                message.setClientListString(clients.stream()
                        .map(serverClient -> "Client ID: " + serverClient.getId() + " -> Port: " + port)
                        .collect(Collectors.joining("\n")));// ! заменил на порт сервера
                client.out.writeObject(objectMapper.writeValueAsString(message));
            } catch (IOException e) {
                System.out.println("Failed to send updated client list: " + e.getMessage());
            }
        }
    }

    private class ServerClient implements Runnable {
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;
        int id;
        int port;
        boolean connected = true;
        private Habitat habitat;

        ServerClient(Socket socket) {
            this.socket = socket;
            this.port = socket.getPort();
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                close();
            }
        }

        public void run() {
            try {
                while (connected) {
                    Object message = in.readObject();
                    if (message instanceof String) {
                        String msg = (String) message;
                        if (msg.startsWith("id:")) {
                            this.id = Integer.parseInt(msg.substring(3));
                            Platform.runLater(() -> textArea.appendText("Client " + this.id + " connected.\n"));
                            sendClientList();
                        } else {
                            handleClientMessage(msg);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> textArea.appendText("Client error: " + e.getMessage() + "\n"));
            } finally {
                close();
                clients.remove(this);
                sendClientList();
            }
        }

        private void handleClientMessage(String message) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Message m = objectMapper.readValue(message, Message.class);

                System.out.println("handle client message: " + m.getMethod() + ", from " + m.getSender());
                if (GET_OBJECTS.equals(m.getMethod())) {
                    getObjects(m.getSender(), m.getTransferObjectCount());
                }
                // TODO: 11.05.2024 add SEND_OBJECTS case to send requested ants to requester
                if (SEND_OBJECTS.equals(m.getMethod())) {
                    sendObjects(m.getSender(), m.getAnts());
                }
                if (REQUEST_CLIENT_LIST.equals(m.getMethod())) {
                    sendClientList();
                } else {
                    Platform.runLater(() -> textArea.appendText("Received from client: " + message + "\n"));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        void close() {
            try {
                connected = false;
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                clients.remove(this);
                sendClientListUpdate(new ArrayList<>());
                Platform.runLater(() -> textArea.appendText("Error closing client connection: " + e.getMessage() + "\n"));
            }
        }
        public int getId() {
            return id;
        }



    }

    private void getObjects(
            int from,
            int cnt
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        ServerClient receiver = clients.stream()
                .filter(serverClient -> serverClient.getId() != from)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing message receiver"));
        Message message = new Message();
        message.setMethod(GET_OBJECTS);
        message.setSender(from);
        message.setTransferObjectCount(cnt);
        try {
            receiver.out.writeObject(objectMapper.writeValueAsString(message));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendObjects(int senderId, List<TransferObject> transferObjects) {
        ServerClient receiver = clients.stream()
                .filter(client -> client.getId() != senderId)
                .findFirst()
                .orElse(null);

        if (receiver == null) {
            System.out.println("No client found with ID: " + senderId);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Message responseMessage = new Message();
        responseMessage.setMethod(SEND_OBJECTS);
        responseMessage.setAnts(transferObjects);
        responseMessage.setTransferObjectCount(transferObjects.size());
        try {
            receiver.out.writeObject(objectMapper.writeValueAsString(responseMessage));
        } catch (IOException e) {
            System.out.println("Failed to send objects: " + e.getMessage());
        }
    }
}
