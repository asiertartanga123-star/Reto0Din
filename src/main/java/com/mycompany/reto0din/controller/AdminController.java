package com.mycompany.reto0din.controller;

import com.mycompany.reto0din.app.App;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.input.MouseEvent;

public class AdminController {

    private static final String DB_URL = System.getenv().getOrDefault(
        "DB_URL", "jdbc:mysql://localhost:3306/tolodb");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "abcd*1234");

    @FXML
    private ListView<Usuario> listaUsuarios;

    @FXML
    private void cerrarSesion() throws java.io.IOException {
        App.setRoot("login");
    }

    @FXML
    private void actualizarClientes() {
        actualizarLista("CLIENTE");
    }

    @FXML
    private void actualizarEmpleados() {
        actualizarLista("EMPLEADO");
    }

    private void actualizarLista(String tipo) {
        String sql = "SELECT dni, nombre, apellido, mail, tlf FROM usuario WHERE tipo = ? "
            + "ORDER BY apellido, nombre";

        listaUsuarios.getItems().clear();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tipo);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    listaUsuarios.getItems().add(new Usuario(
                        result.getString("dni"), result.getString("nombre"),
                        result.getString("apellido"), result.getString("mail"),
                        result.getString("tlf"), tipo));
                }
            }
        } catch (SQLException exception) {
            mostrarError("No se pudo actualizar la lista. Comprueba MySQL y la configuración de conexión.");
        }
    }

    @FXML
    private void editarSeleccionado(MouseEvent evento) {
        Node objetivo = (Node) evento.getTarget();
        while (objetivo != null && !(objetivo instanceof ListCell)) {
            objetivo = objetivo.getParent();
        }
        if (!(objetivo instanceof ListCell)) {
            return;
        }

        Usuario usuario = listaUsuarios.getSelectionModel().getSelectedItem();
        if (usuario == null) {
            return;
        }

        TextField correo = new TextField(usuario.mail);
        TextField telefono = new TextField(usuario.telefono == null ? "" : usuario.telefono);
        Dialog<ButtonType> dialogo = new Dialog<>();
        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialogo.setTitle("Editar usuario");
        dialogo.setHeaderText(usuario.nombre + " " + usuario.apellido + " (" + usuario.dni + ")");
        dialogo.getDialogPane().getButtonTypes().addAll(guardar, ButtonType.CANCEL);

        GridPane campos = new GridPane();
        campos.setHgap(10.0);
        campos.setVgap(10.0);
        campos.setPadding(new Insets(10.0));
        campos.add(new Label("Correo electrónico"), 0, 0);
        campos.add(correo, 1, 0);
        campos.add(new Label("Teléfono"), 0, 1);
        campos.add(telefono, 1, 1);
        dialogo.getDialogPane().setContent(campos);

        Optional<ButtonType> resultado = dialogo.showAndWait();
        if (!resultado.isPresent() || resultado.get() != guardar) {
            return;
        }

        Integer numeroTelefono = null;
        if (!telefono.getText().trim().isEmpty()) {
            try {
                numeroTelefono = Integer.valueOf(telefono.getText().trim());
            } catch (NumberFormatException exception) {
                mostrarError("El teléfono debe ser un número válido.");
                return;
            }
        }

        guardarCambios(usuario, correo.getText().trim(), numeroTelefono);
    }

    private void guardarCambios(Usuario usuario, String correo, Integer telefono) {
        String sql = "UPDATE usuario SET mail = ?, tlf = ? WHERE dni = ? AND tipo = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, correo);
            if (telefono == null) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, telefono);
            }
            statement.setString(3, usuario.dni);
            statement.setString(4, usuario.tipo);

            if (statement.executeUpdate() == 0) {
                mostrarError("No se pudo encontrar el usuario para guardar los cambios.");
                return;
            }
            actualizarLista(usuario.tipo);
        } catch (SQLException exception) {
            mostrarError("No se pudieron guardar los cambios. Comprueba la conexión con MySQL.");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private static class Usuario {
        private final String dni;
        private final String nombre;
        private final String apellido;
        private final String mail;
        private final String telefono;
        private final String tipo;

        private Usuario(String dni, String nombre, String apellido, String mail,
                        String telefono, String tipo) {
            this.dni = dni;
            this.nombre = nombre;
            this.apellido = apellido;
            this.mail = mail;
            this.telefono = telefono;
            this.tipo = tipo;
        }

        @Override
        public String toString() {
            return String.format("%s | %s %s | %s | Tel: %s", dni, nombre, apellido,
                mail == null ? "" : mail, telefono == null ? "" : telefono);
        }
    }
}
