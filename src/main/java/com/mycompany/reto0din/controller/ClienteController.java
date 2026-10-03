package com.mycompany.reto0din.controller;

import com.mycompany.reto0din.app.App;
import com.mycompany.reto0din.dao.UsuarioDAO;
import com.mycompany.reto0din.model.Usuario;
import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Gestiona la consulta y modificación de los datos del perfil del cliente.
 *
 * @author Ekain
 */
public class ClienteController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

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

    /** Carga en la pantalla los datos del cliente que inició sesión. */
    @FXML
    private void initialize() {
        try {
            Usuario usuario = usuarioDAO.buscarPorCorreo(emailCliente);
            if (usuario != null) {
                lblBienvenida.setText("Te damos la bienvenida, " + usuario.getNombre() + "!");
                lblDni.setText(usuario.getDni());
                lblNombre.setText(usuario.getNombre());
                lblApellido.setText(usuario.getApellido());
                lblPais.setText(usuario.getPais());
                txtMail.setText(usuario.getCorreo());
                txtTlf.setText(usuario.getTelefono());
            } else {
                lblBienvenida.setText("No se encontró el usuario");
            }

        } catch (SQLException exception) {
            lblBienvenida.setText("Error al cargar los datos");
        }
    }

    /** Valida y guarda los cambios del correo electrónico y el teléfono. */
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

        try {
            usuarioDAO.actualizarDatosCliente(lblDni.getText(), nuevoMail, nuevoTlf);
            emailCliente = nuevoMail;
            mostrarAlerta(Alert.AlertType.INFORMATION, "Datos actualizados.");

        } catch (SQLException exception) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudieron guardar los cambios.");
        }
    }

    /** Cierra la sesión del cliente y vuelve al inicio de sesión.
     *
     * @throws IOException si no se puede cargar la pantalla de inicio de sesión
     */
    @FXML
    private void cerrarSesion() throws IOException {
        emailCliente = null;
        App.setRoot("login");
    }

    /** Muestra una alerta con el mensaje indicado.
     *
     * @param tipo tipo de alerta que se va a mostrar
     * @param mensaje contenido de la alerta
     */
    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}