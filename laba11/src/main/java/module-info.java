module com.example.laba11 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

//    requires org.kordamp.bootstrapfx.core;

    opens my.snole.laba11 to javafx.fxml;
    exports my.snole.laba11;
    exports my.snole.laba11.model.ant;
    exports my.snole.laba11.UIController;
    opens my.snole.laba11.UIController;
    exports my.snole.laba11.service;
    opens my.snole.laba11.service;
    opens my.snole.laba11.model.ant to javafx.fxml;
    exports my.snole.laba11.model;
    opens my.snole.laba11.model to javafx.fxml;
    exports my.snole.laba11.model.ant.AI;
    opens my.snole.laba11.model.ant.AI to javafx.fxml;

    opens my.snole.laba11.server to javafx.fxml;
    exports my.snole.laba11.server;


}