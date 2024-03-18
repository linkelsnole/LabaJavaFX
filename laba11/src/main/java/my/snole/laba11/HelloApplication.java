package my.snole.laba11;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import my.snole.laba11.UIController.UIController;

import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("habitat.fxml"));
        stage.setResizable(false);
        Scene scene = new Scene(fxmlLoader.load(), 1100, 640);
        UIController controller = fxmlLoader.getController();
        stage.setTitle("Ant Simulation");
        scene.setOnKeyPressed(controller::run);

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}