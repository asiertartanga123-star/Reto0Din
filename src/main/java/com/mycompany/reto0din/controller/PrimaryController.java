package com.mycompany.reto0din.controller;

import com.mycompany.reto0din.app.App;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void cerrarSesion() throws java.io.IOException {
        App.setRoot("login");
    }
}
