module com.makarova.secondsemestrwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;

    opens com.makarova.secondsemestrwork.controller to javafx.fxml;
    exports com.makarova.secondsemestrwork;
}
