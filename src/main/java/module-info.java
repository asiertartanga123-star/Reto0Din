module com.mycompany.reto0din {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.base;

    opens com.mycompany.reto0din.controller to javafx.fxml;
    exports com.mycompany.reto0din.app;
}
