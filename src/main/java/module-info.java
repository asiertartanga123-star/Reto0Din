module com.mycompany.reto0din {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.mycompany.reto0din to javafx.fxml;
    exports com.mycompany.reto0din;
}
