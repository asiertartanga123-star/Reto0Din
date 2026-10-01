package com.mycompany.reto0din.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;
import java.util.regex.Pattern;
import com.mycompany.reto0din.app.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroController {

    private static final String DB_URL = System.getenv().getOrDefault(
        "DB_URL", "jdbc:mysql://localhost:3306/tolodb");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");

    private static final Pattern PATRON_DNI = Pattern.compile("^\\d{8}[A-Za-z]$");
    private static final Pattern PATRON_EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^[6-9]\\d{8}$");
    private static final Pattern PATRON_TARJETA = Pattern.compile("^\\d{9,16}$");

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmar;
    @FXML
    private ComboBox<String> cmbPais;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtTarjeta;

    @FXML
    private void initialize() {
        cmbPais.getItems().addAll("Espana", "Mexico", "Argentina", "Chile", "Colombia",
            "Peru", "Francia", "Portugal", "Italia");
    }

    @FXML
    private void registrar() throws IOException {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String dni = txtDni.getText().trim().toUpperCase();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirmar = txtConfirmar.getText();
        String pais = cmbPais.getValue();
        String telefono = txtTelefono.getText().trim();
        String tarjeta = txtTarjeta.getText().trim();

        String error = validar(nombre, apellido, dni, email, password, confirmar, pais,
            telefono, tarjeta);
        if (error != null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incorrectos", error);
            return;
        }

        String sqlInsert = "INSERT INTO usuario (dni, nombre, apellido, mail, contrasenia, "
            + "pais, tlf, tarjeta, tipo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'CLIENTE')";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (existe(connection, "SELECT 1 FROM usuario WHERE dni = ?", dni)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Usuario existente",
                    "Ya existe una cuenta con ese DNI.");
                return;
            }
            if (existe(connection, "SELECT 1 FROM usuario WHERE mail = ?", email)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Usuario existente",
                    "Ya existe una cuenta con ese email.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
                statement.setString(1, dni);
                statement.setString(2, nombre);
                statement.setString(3, apellido);
                statement.setString(4, email);
                statement.setString(5, password);
                statement.setString(6, pais);
                statement.setInt(7, Integer.parseInt(telefono));
                if (tarjeta.isEmpty()) {
                    statement.setNull(8, Types.VARCHAR);
                } else {
                    statement.setString(8, tarjeta);
                }
                statement.executeUpdate();
            }
        } catch (SQLIntegrityConstraintViolationException exception) {
            mostrarAlerta(Alert.AlertType.ERROR, "Usuario existente",
                "Ya existe una cuenta con ese DNI.");
            return;
        } catch (SQLException exception) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                "No se pudo registrar el usuario. Comprueba que MySQL esté iniciado, "
                + "revisa DB_URL, DB_USER y DB_PASSWORD y que los datos quepan en la tabla.");
            return;
        }

        mostrarAlerta(Alert.AlertType.INFORMATION, "Registro completado",
            "Tu cuenta se ha creado correctamente.");
        App.setRoot("login");
    }

    @FXML
    private void volverAlLogin() throws IOException {
        App.setRoot("login");
    }

    /** Devuelve el mensaje del primer error encontrado, o null si todo es correcto. */
    private String validar(String nombre, String apellido, String dni, String email,
                           String password, String confirmar, String pais,
                           String telefono, String tarjeta) {
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || email.isEmpty()
            || password.isEmpty() || confirmar.isEmpty() || pais == null
            || telefono.isEmpty()) {
            return "Rellena todos los campos obligatorios.";
        }
        if (nombre.length() > 20 || apellido.length() > 20) {
            return "El nombre y el apellido no pueden superar los 20 caracteres.";
        }
        if (!PATRON_DNI.matcher(dni).matches()) {
            return "El DNI debe tener 8 números y una letra (por ejemplo, 12345678A).";
        }
        if (email.length() > 50 || !PATRON_EMAIL.matcher(email).matches()) {
            return "Introduce un email válido (máximo 50 caracteres).";
        }
        if (password.length() < 6 || password.length() > 50) {
            return "La contraseña debe tener entre 6 y 50 caracteres.";
        }
        if (!password.equals(confirmar)) {
            return "Las contraseñas no coinciden.";
        }
        if (!PATRON_TELEFONO.matcher(telefono).matches()) {
            return "El teléfono debe tener 9 dígitos y empezar por 6, 7, 8 o 9.";
        }
        if (!tarjeta.isEmpty() && !PATRON_TARJETA.matcher(tarjeta).matches()) {
            return "La tarjeta debe contener solo números (entre 9 y 16 dígitos).";
        }
        return null;
    }

    private boolean existe(Connection connection, String sql, String valor) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, valor);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}