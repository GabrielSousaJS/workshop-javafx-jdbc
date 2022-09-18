module com.example.workshopjavafxjdbc {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.workshopjavafxjdbc to javafx.fxml;
    exports com.example.workshopjavafxjdbc;
    exports application;
    opens application to javafx.fxml;
    exports gui.controller;
    opens gui.controller to javafx.fxml;
    opens model.entities to javafx.base;
}