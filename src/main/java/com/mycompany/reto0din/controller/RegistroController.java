package com.mycompany.reto0din.controller;

import java.io.IOException;
import com.mycompany.reto0din.app.App;
import javafx.fxml.FXML;

public class RegistroController {

    @FXML
    private void volverAlLogin() throws IOException {
        App.setRoot("login");
    }
}