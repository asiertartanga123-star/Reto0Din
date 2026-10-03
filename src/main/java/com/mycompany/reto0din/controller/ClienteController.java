package com.mycompany.reto0din.controller;

import com.mycompany.reto0din.app.App;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ClienteController {

    private static final String DB_URL = System.getenv().getOrDefault(
        "DB_URL", "jdbc:mysql://localhost:3306/tolodb");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "abcd*1234");

    public static String emailCliente;

    private String dniCliente;
    private TextField txtMail;
    private TextField txtTlf;

    @FXML
    private Label lblBienvenida;
    @FXML
    private GridPane datos;

    @FXML
    private void initialize() {
        String sql = "SELECT dni, nombre, apellido, mail, pais, tlf FROM usuario WHERE mail = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, emailCliente);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    dniCliente = result.getString("dni");
                    lblBienvenida.setText("Te damos la bienvenida, " + result.getString("nombre") + "!");

                    datos.getChildren().clear();

                    // Solo lectura
                    anadirFila(0, "DNI", dniCliente);
                    anadirFila(1, "Nombre", result.getString("nombre"));
                    anadirFila(2, "Apellido", result.getString("apellido"));
                    anadirFila(3, "País", result.getString("pais"));

                    // Editables
                    txtMail = new TextField(result.getString("mail"));
                    txtTlf = new TextField(String.valueOf(result.getInt("tlf")));
                    anadirCampo(4, "Correo", txtMail);
                    anadirCampo(5, "Teléfono", txtTlf);

                    Button btnGuardar = new Button("Guardar cambios");
                    btnGuardar.setOnAction(e -> guardarCambios());
                    datos.add(btnGuardar, 1, 6);
                } else {
                    lblBienvenida.setText("No se encontró el usuario: " + emailCliente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblBienvenida.setText("Error al cargar los datos.");
        }
    }

    private void guardarCambios() {
        String nuevoMail = txtMail.getText().trim();
        String textoTlf = txtTlf.getText().trim();

        if (nuevoMail.isEmpty() || textoTlf.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos",
                "Rellena el correo y el teléfono.");
            return;
        }

        int nuevoTlf;
        try {
            nuevoTlf = Integer.parseInt(textoTlf);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Teléfono no válido",
                "El teléfono solo puede contener números.");
            return;
        }

        String sql = "UPDATE usuario SET mail = ?, tlf = ? WHERE dni = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nuevoMail);
            statement.setInt(2, nuevoTlf);
            statement.setString(3, dniCliente);
            statement.executeUpdate();

            emailCliente = nuevoMail;
            mostrarAlerta(Alert.AlertType.INFORMATION, "Correcto", "Datos actualizados.");
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron guardar los cambios.");
        }
    }

    private void anadirFila(int fila, String etiqueta, String valor) {
        Label lblEtiqueta = new Label(etiqueta + ":");
        lblEtiqueta.setStyle("-fx-font-weight: bold;");
        datos.add(lblEtiqueta, 0, fila);
        datos.add(new Label(valor), 1, fila);
    }

    private void anadirCampo(int fila, String etiqueta, TextField campo) {
        Label lblEtiqueta = new Label(etiqueta + ":");
        lblEtiqueta.setStyle("-fx-font-weight: bold;");
        datos.add(lblEtiqueta, 0, fila);
        datos.add(campo, 1, fila);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void cerrarSesion() throws IOException {
        emailCliente = null;
        App.setRoot("login");
    }
}