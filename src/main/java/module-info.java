module com.makarova.secondsemestrwork {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.makarova.secondsemestrwork to javafx.fxml;
    exports com.makarova.secondsemestrwork;
}