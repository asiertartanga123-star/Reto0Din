package com.mycompany.reto0din.controller;

import java.io.IOException;
import com.mycompany.reto0din.app.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class IniciarSesionController {

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;

    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}