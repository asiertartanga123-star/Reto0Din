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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ClienteController {

    private static final String URL = "jdbc:mysql://localhost:3306/tolodb";
    private static final String USER = "root";
    private static final String PASSWORD = "abcd*1234";

    // Se rellena desde el login
    public static String emailCliente;

    @FXML
    private Label lblBienvenida;
    @FXML
    private Label lblDni;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblApellido;
    @FXML
    private Label lblPais;
    @FXML
    private TextField txtMail;
    @FXML
    private TextField txtTlf;

    @FXML
    private void initialize() {
        String sql = "SELECT dni, nombre, apellido, mail, pais, tlf FROM usuario WHERE mail = ?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emailCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblBienvenida.setText("Te damos la bienvenida, " + rs.getString("nombre") + "!");
                lblDni.setText(rs.getString("dni"));
                lblNombre.setText(rs.getString("nombre"));
                lblApellido.setText(rs.getString("apellido"));
                lblPais.setText(rs.getString("pais"));
                txtMail.setText(rs.getString("mail"));
                txtTlf.setText(String.valueOf(rs.getInt("tlf")));
            } else {
                lblBienvenida.setText("No se encontró el usuario");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lblBienvenida.setText("Error al cargar los datos");
        }
    }

    @FXML
    private void guardarCambios() {
        String nuevoMail = txtMail.getText().trim();
        String textoTlf = txtTlf.getText().trim();

        if (nuevoMail.isEmpty() || textoTlf.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Rellena el correo y el teléfono.");
            return;
        }

        int nuevoTlf;
        try {
            nuevoTlf = Integer.parseInt(textoTlf);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "El teléfono solo puede contener números.");
            return;
        }

        String sql = "UPDATE usuario SET mail = ?, tlf = ? WHERE dni = ?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoMail);
            ps.setInt(2, nuevoTlf);
            ps.setString(3, lblDni.getText());
            ps.executeUpdate();

            emailCliente = nuevoMail;
            mostrarAlerta(Alert.AlertType.INFORMATION, "Datos actualizados.");

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudieron guardar los cambios.");
        }
    }

    @FXML
    private void cerrarSesion() throws IOException {
        emailCliente = null;
        App.setRoot("login");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}