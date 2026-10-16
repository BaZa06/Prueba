module ni.edu.uam.prueba1s9 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens ni.edu.uam.prueba1s9.model to javafx.base;

    exports ni.edu.uam.prueba1s9.controller to javafx.fxml;
    opens ni.edu.uam.prueba1s9.controller to javafx.fxml;


    opens ni.edu.uam.prueba1s9 to javafx.fxml;
    exports ni.edu.uam.prueba1s9;
}