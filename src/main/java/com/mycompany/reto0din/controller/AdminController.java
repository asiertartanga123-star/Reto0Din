package com.mycompany.reto0din.controller;

import com.mycompany.reto0din.app.App;
import com.mycompany.reto0din.dao.UsuarioDAO;
import com.mycompany.reto0din.model.Usuario;
import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

public class AdminController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private ListView<Usuario> listaUsuarios;

    @FXML
    private void actualizarClientes() {
        actualizarLista("CLIENTE");
    }

    @FXML
    private void actualizarEmpleados() {
        actualizarLista("EMPLEADO");
    }

    @FXML
    private void cerrarSesion() throws IOException {
        App.setRoot("login");
    }

    private void actualizarLista(String tipo) {
        try {
            listaUsuarios.getItems().setAll(usuarioDAO.listarPorTipo(tipo));
        } catch (SQLException exception) {
            mostrarError("No se pudo actualizar la lista. Comprueba MySQL y la conexión.");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}