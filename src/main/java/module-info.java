module com.makarova.secondsemestrwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.desktop;
    requires com.google.gson;

    opens com.makarova.secondsemestrwork.controller to javafx.fxml;
    opens com.makarova.secondsemestrwork.entity to com.google.gson;
    exports com.makarova.secondsemestrwork;
}
