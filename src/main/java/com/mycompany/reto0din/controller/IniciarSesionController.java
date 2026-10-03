package com.mycompany.reto0din.controller;

import java.io.IOException;
import com.mycompany.reto0din.dao.UsuarioDAO;
import java.sql.SQLException;
import com.mycompany.reto0din.app.App;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class IniciarSesionController {


    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private static final String DB_URL = System.getenv().getOrDefault(
        "DB_URL", "jdbc:mysql://localhost:3306/tolodb");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "abcd*1234");


    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;

    @FXML
    private void iniciarSesion() throws IOException {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos",
                    "Introduce tu email y contraseña.");
            return;
        }

        try {
            String tipo = usuarioDAO.autenticar(email, password);
            if (tipo == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Acceso denegado",
                        "El email o la contraseña no son correctos.");
            } else if ("ADMIN".equals(tipo)) {
                App.setRoot("admin");
            } else if ("CLIENTE".equals(tipo)) {
                ClienteController.emailCliente = email;
                App.setRoot("cliente");
            } else if("EMPLEADO".equals(tipo)){
                App.setRoot("empleado");
            }else {
                mostrarAlerta(Alert.AlertType.WARNING, "Acceso no permitido",
                        "Esta cuenta no tiene acceso al panel de administración.");
            }
        } catch (SQLException exception) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                    "No se pudo conectar con tolodb. Comprueba que MySQL esté iniciado y revisa DB_URL, DB_USER y DB_PASSWORD.");
        }
    }

    @FXML
    private void abrirRegistro() throws IOException {
        App.setRoot("register");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
