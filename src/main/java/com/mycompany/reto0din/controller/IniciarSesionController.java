package com.mycompany.reto0din.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mycompany.reto0din.app.App;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class IniciarSesionController {

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

        String sql = "SELECT tipo FROM usuario WHERE mail = ? AND contrasenia = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Acceso denegado",
                        "El email o la contraseña no son correctos.");
                } else if ("ADMIN".equals(result.getString("tipo"))) {
                    App.setRoot("admin");
                } else if ("CLIENTE".equals(result.getString("tipo"))) {
                    App.setRoot("cliente");
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Acceso no permitido",
                        "Esta cuenta no tiene acceso habilitado.");
                }
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