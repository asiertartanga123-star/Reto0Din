package com.mycompany.reto0din.controller;

import com.mycompany.reto0din.app.App;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ClienteController {

    public static String nombreCliente;

    @FXML
    private Label lblBienvenida;

    @FXML
    private void initialize() {
        lblBienvenida.setText("Te damos la bienvenida, " + nombreCliente + "!");
    }

    @FXML
    private void cerrarSesion() throws IOException {
        App.setRoot("login");
    }
}
