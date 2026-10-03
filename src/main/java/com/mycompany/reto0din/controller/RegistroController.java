package com.mycompany.reto0din.controller;


import com.mycompany.reto0din.app.App;
import com.mycompany.reto0din.dao.UsuarioDAO;
import java.io.IOException;
import java.sql.SQLException;
import Exceptions.ValidarDniException;
import Exceptions.ValidarEmailException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Gestiona la validación de los datos y el registro de nuevos clientes.
 *
 * @author Jon Ander
 */
public class RegistroController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

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

    /** Rellena el selector de países al abrir la pantalla de registro. */
    @FXML
    private void initialize() {
        cmbPais.getItems().addAll("Espana", "Mexico", "Argentina", "Chile", "Colombia");
    }

    /** Valida los datos introducidos, registra el cliente y muestra el resultado. */
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

        try {
            usuarioDAO.registrarCliente(dni, nombre, apellido, email, password, pais,
                Integer.parseInt(telefono), tarjeta.isEmpty() ? null : tarjeta);
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

    /** Vuelve a la pantalla de inicio de sesión.
     *
     * @throws IOException si no se puede cargar la pantalla de inicio de sesión
     */
    @FXML
    private void volverAlLogin() throws IOException {
        App.setRoot("login");
    }

    /** Comprueba el formato y la letra de control del DNI.
     *
     * @param dni DNI que se va a validar
     * @throws ValidarDniException si el formato o la letra del DNI no son válidos
     */
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

    /** Comprueba que el correo tenga un formato válido.
     *
     * @param email correo electrónico que se va a validar
     * @throws ValidarEmailException si el correo no tiene un formato válido
     */
    private void validarEmail(String email) throws ValidarEmailException {
        String PATRON_EMAIL = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(PATRON_EMAIL)) {
            throw new ValidarEmailException("El email no es válido (ejemplo: nombre@correo.com).");
        }
    }

    /** Muestra una alerta con el tipo, título y mensaje indicados.
     *
     * @param tipo tipo de alerta que se va a mostrar
     * @param titulo título de la alerta
     * @param mensaje contenido de la alerta
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}