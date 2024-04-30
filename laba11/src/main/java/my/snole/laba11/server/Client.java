package my.snole.laba11.server;

import javafx.application.Platform;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.Habitat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
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
                    handleServerMessage((String) obj);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            Platform.runLater(() -> System.out.println("Connection error: " + e.getMessage()));
            disconnect();
        }
    }

    private void handleServerMessage(String message) {
        String[] parts = message.split(":");
        switch (parts[0]) {
            case "list":
                Platform.runLater(() -> updateClientList(parts[1]));
                break;
            case "giveObject":
                requestAnts(Integer.parseInt(parts[1]));
                break;
            case "getObject":
                receiveAnts(parts[1]);
                break;
        }
    }

    private void updateClientList(String clientData) {
        // --TODO обновление пользовательского интерфейса с помощью нового списка клиентов
    }

    private void requestAnts(int numberOfAnts) {
        try {
            List<Ant> list = new ArrayList<>(SingletonDynamicArray.getInstance().getAntsList());

            // Если список муравьёв меньше, чем запрошенное количество, отправляем все, что у нас есть
            if (list.size() <= numberOfAnts) {
                out.writeObject(new ArrayList<>(list));
            } else {
                // Иначе выбираем случайные элементы
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
            } catch (IOException e) {
                Platform.runLater(() -> System.out.println("Error closing connection: " + e.getMessage()));
            }
        }
    }
}

