module com.example.workshopjavafxjdbc {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.workshopjavafxjdbc to javafx.fxml;
    exports com.example.workshopjavafxjdbc;
    exports com.example.workshopjavafxjdbc.application;
    opens com.example.workshopjavafxjdbc.application to javafx.fxml;
}