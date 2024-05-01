package my.snole.laba11;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import my.snole.laba11.UIController.UIController;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.service.Config;
import my.snole.laba11.service.Console;

import java.io.IOException;


public class HelloApplication extends Application {
    public static HelloApplication instance;
    public AliveAntsDialog aliveAntsDialog;

    public Console console;
    private Stage primaryStage;
    private Stage stage;

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        instance = this;
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("habitat.fxml"));
        stage.setResizable(false);
        Scene scene = new Scene(fxmlLoader.load(), 1100, 640);
        UIController controller = fxmlLoader.getController();
        SingletonDynamicArray.getInstance().getConfig().loadFromFile();
        stage.setTitle("Ant Simulation");
        scene.setOnKeyPressed(controller::run);

        stage.setOnCloseRequest(event -> {
            SingletonDynamicArray.getInstance().getConfig().saveInFile();
            Platform.exit();
            System.exit(0);
        });

        stage.setScene(scene);
        stage.show();

    }
    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch();
    }
}