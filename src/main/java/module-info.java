module ghosking.lormaster {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires okhttp;
    requires org.json;
    requires org.apache.commons.codec;

    opens ghosking.lormaster to javafx.fxml;
    exports ghosking.lormaster;
    exports ghosking.lormaster.lor;
    exports ghosking.lormaster.controller;
    opens ghosking.lormaster.lor to javafx.fxml;
    opens ghosking.lormaster.controller to javafx.fxml;
}