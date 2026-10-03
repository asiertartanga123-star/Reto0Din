package com.mycompany.reto0din.controller;


import com.mycompany.reto0din.app.App;
import java.io.IOException;
import javafx.fxml.FXML;   
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.mycompany.reto0din.app.App;
import Exceptions.ValidarDniException;
import Exceptions.ValidarEmailException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroController {

    private static final String DB_URL = System.getenv().getOrDefault(
        "DB_URL", "jdbc:mysql://localhost:3306/tolodb");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "abcd*1234");

    // Componentes del register.fxml (el nombre tiene que ser igual que el fx:id)
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

    // Se ejecuta solo al abrir la pantalla. Rellena el desplegable de paises
    @FXML
    private void initialize() {
        cmbPais.getItems().addAll("Espana", "Mexico", "Argentina", "Chile", "Colombia");
    }

    // Boton "Crear cuenta"
    @FXML
    private void registrar() throws IOException {
        // Cojo lo que ha escrito el usuario
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String dni = txtDni.getText().toUpperCase();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirmar = txtConfirmar.getText();
        String pais = cmbPais.getValue();
        String telefono = txtTelefono.getText();
        String tarjeta = txtTarjeta.getText();

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmar.isEmpty() || pais == null || telefono.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Rellena todos los campos.");
            return;
        }

        try {
            validarDni(dni);
            validarEmail(email);
        } catch (ValidarDniException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "DNI incorrecto", e.getMessage());
            return;
        } catch (ValidarEmailException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Email incorrecto", e.getMessage());
            return;
        }

        if (!password.equals(confirmar)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Contraseña", "Las contraseñas no coinciden.");
            return;
        }
        if (!telefono.matches("[0-9]{9}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Teléfono incorrecto", "El teléfono debe tener 9 números.");
            return;
        }
        if (!tarjeta.isEmpty() && !tarjeta.matches("[0-9]+")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Tarjeta incorrecta", "La tarjeta solo puede tener números.");
            return;
        }

        String sql = "INSERT INTO usuario (dni, nombre, apellido, mail, contrasenia, pais, tlf, tarjeta, tipo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'CLIENTE')";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dni);
            statement.setString(2, nombre);
            statement.setString(3, apellido);
            statement.setString(4, email);
            statement.setString(5, password);
            statement.setString(6, pais);
            statement.setInt(7, Integer.parseInt(telefono));
            if (tarjeta.isEmpty()) {
                statement.setString(8, null);
            } else {
                statement.setString(8, tarjeta);
            }
            statement.executeUpdate();
        } catch (SQLException exception) {
            // 1062 es el codigo de MySQL para "entrada duplicada" (el DNI ya existe)
            if (exception.getErrorCode() == 1062) {
                mostrarAlerta(Alert.AlertType.ERROR, "Usuario existente",
                    "Ya existe una cuenta con ese DNI.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                    "No se pudo conectar con tolodb. Comprueba que MySQL esté iniciado y revisa DB_URL, DB_USER y DB_PASSWORD.");
            }
            return;
        }

        mostrarAlerta(Alert.AlertType.INFORMATION, "Registro", "Usuario registrado correctamente.");
        App.setRoot("login");
    }

    @FXML
    private void volverAlLogin() throws IOException {
        App.setRoot("login");
    }

    //Excepciones
    private void validarDni(String dni) throws ValidarDniException {
        if (!dni.matches("^[0-9]{8}[A-Z]$")) {
            throw new ValidarDniException("El DNI debe tener 8 dígitos seguidos de una letra mayúscula.");
        }

        int numero = Integer.parseInt(dni.substring(0, 8));
        char letra = dni.charAt(8);
        char[] letras = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H',
                'L', 'C', 'K', 'E' };

        if (letra != letras[numero % 23]) {
            throw new ValidarDniException("La letra del DNI no es correcta.");
        }
    }

    private void validarEmail(String email) throws ValidarEmailException {
        String PATRON_EMAIL = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(PATRON_EMAIL)) {
            throw new ValidarEmailException("El email no es válido (ejemplo: nombre@correo.com).");
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