package my.snole.laba11.server;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;



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

    private void stopServer() {
        try {
            serverRunning = false;
            for (ServerClient client : clients) {
                client.close();
            }
            clients.clear();
            serverSocket.close();
            thread.interrupt();
        } catch (IOException e) {
            Platform.runLater(() -> textArea.appendText("Error stopping server: " + e.getMessage() + "\n"));
        }
    }

    private class ServerClient implements Runnable {
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;
        int id;
        boolean connected = true;

        ServerClient(Socket socket) {
            this.socket = socket;
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
                        handleClientMessage((String) message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> textArea.appendText("Client error: " + e.getMessage() + "\n"));
            } finally {
                close();
            }
        }

        private void handleClientMessage(String message) {
            Platform.runLater(() -> textArea.appendText("Received from client: " + message + "\n"));
        }

        void close() {
            try {
                connected = false;
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                Platform.runLater(() -> textArea.appendText("Error closing client connection: " + e.getMessage() + "\n"));
            }
        }
    }

}
