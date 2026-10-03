package com.mycompany.reto0din.controller;

import com.mycompany.reto0din.app.App;
import com.mycompany.reto0din.dao.UsuarioDAO;
import com.mycompany.reto0din.model.Usuario;
import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

/**
 * Controla la pantalla del empleado y la consulta de usuarios por tipo.
 *
 * @author Ricardo
 */
public class EmpleadoController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private ListView<Usuario> listaUsuarios;

    /** Carga en la lista los usuarios con cuenta de cliente. */
    @FXML
    private void actualizarClientes() {
        actualizarLista("CLIENTE");
    }

    /** Carga en la lista los usuarios con cuenta de empleado. */
    @FXML
    private void actualizarEmpleados() {
        actualizarLista("EMPLEADO");
    }

    /** Vuelve a la pantalla de inicio de sesión.
     *
     * @throws IOException si no se puede cargar la pantalla de inicio de sesión
     */
    @FXML
    private void cerrarSesion() throws IOException {
        App.setRoot("login");
    }

    /** Actualiza la lista con los usuarios del tipo indicado.
     *
     * @param tipo tipo de usuario que se va a consultar
     */
    private void actualizarLista(String tipo) {
        try {
            listaUsuarios.getItems().setAll(usuarioDAO.listarPorTipo(tipo));
        } catch (SQLException exception) {
            mostrarError("No se pudo actualizar la lista. Comprueba MySQL y la conexión.");
        }
    }

    /** Muestra un cuadro de error con el mensaje indicado.
     *
     * @param mensaje texto que se mostrara en la alerta
     */
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}