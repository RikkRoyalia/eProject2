module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires annotations;
    requires java.desktop;
    requires javafx.base;


//    opens com.example.demo to javafx.fxml;
//    exports com.example.demo;

    opens SHAIF to javafx.graphics;
    exports SHAIF;
    exports SHAIF.game;
    opens SHAIF.game to javafx.graphics;
    exports SHAIF.controller;
    opens SHAIF.controller to javafx.graphics;
}