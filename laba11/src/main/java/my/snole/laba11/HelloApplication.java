package my.snole.laba11;
import my.snole.laba11.UIController.UIController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;



public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("habitat.fxml"));
        stage.setResizable(false);
        Scene scene = new Scene(fxmlLoader.load(), 800, 640);
        Habitat controller = fxmlLoader.getController();
        stage.setTitle("Ant Simulation");
        scene.setOnKeyPressed(controller::run);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}