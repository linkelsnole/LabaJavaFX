module com.example.laba11 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens my.snole.laba11 to javafx.fxml;
    exports my.snole.laba11;
    exports my.snole.laba11.Ant;
    opens my.snole.laba11.Ant to javafx.fxml;
}