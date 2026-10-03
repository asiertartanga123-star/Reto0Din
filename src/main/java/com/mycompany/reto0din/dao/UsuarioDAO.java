package com.mycompany.reto0din.dao;

import com.mycompany.reto0din.model.Usuario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO con las consultas de usuario de la aplicacion. */
public final class UsuarioDAO {

    private static final String DB_URL = System.getenv().getOrDefault(
        "DB_URL", "jdbc:mysql://localhost:3306/tolodb");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "abcd*1234");

    /**
     * @param correo correo introducido
     * @param contrasenia contrasena introducida
     * @return tipo de cuenta o {@code null} si las credenciales no coinciden
     * @throws SQLException si falla la consulta
     */
    public String autenticar(String correo, String contrasenia) throws SQLException {
        String sql = "SELECT tipo FROM usuario WHERE mail = ? AND contrasenia = ?";
        try (Connection conexion = abrirConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, correo);
            sentencia.setString(2, contrasenia);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getString("tipo");
                }
                return null;
            }
        }
    }

    /**
     * @param tipo categoria que se va a consultar
     * @return usuarios ordenados por apellido y nombre
     * @throws SQLException si falla la consulta
     */
    public List<Usuario> listarPorTipo(String tipo) throws SQLException {
        String sql = "SELECT dni, nombre, apellido, mail, tlf FROM usuario WHERE tipo = ? "
            + "ORDER BY apellido, nombre";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexion = abrirConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, tipo);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    usuarios.add(new Usuario(
                        resultado.getString("dni"), resultado.getString("nombre"),
                        resultado.getString("apellido"), resultado.getString("mail"),
                        resultado.getString("tlf"), tipo));
                }
            }
        }
        return usuarios;
    }

    public void registrarCliente(String dni, String nombre, String apellido, String correo,
                                 String contrasenia, String pais, int telefono, String tarjeta)
            throws SQLException {
        String sql = "INSERT INTO usuario (dni, nombre, apellido, mail, contrasenia, pais, tlf, tarjeta, tipo) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'CLIENTE')";
        try (Connection conexion = abrirConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, dni);
            sentencia.setString(2, nombre);
            sentencia.setString(3, apellido);
            sentencia.setString(4, correo);
            sentencia.setString(5, contrasenia);
            sentencia.setString(6, pais);
            sentencia.setInt(7, telefono);
            sentencia.setString(8, tarjeta);
            sentencia.executeUpdate();
        }
    }

    public Usuario buscarPorCorreo(String correo) throws SQLException {
        String sql = "SELECT dni, nombre, apellido, mail, pais, tlf, tipo FROM usuario WHERE mail = ?";
        try (Connection conexion = abrirConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, correo);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return null;
                }
                return new Usuario(
                    resultado.getString("dni"), resultado.getString("nombre"),
                    resultado.getString("apellido"), resultado.getString("mail"),
                    resultado.getString("tlf"), resultado.getString("tipo"),
                    resultado.getString("pais"));
            }
        }
    }

    public void actualizarDatosCliente(String dni, String correo, int telefono) throws SQLException {
        String sql = "UPDATE usuario SET mail = ?, tlf = ? WHERE dni = ?";
        try (Connection conexion = abrirConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, correo);
            sentencia.setInt(2, telefono);
            sentencia.setString(3, dni);
            sentencia.executeUpdate();
        }
    }

    /**
     * @return conexion a la base de datos
     * @throws SQLException si no se puede establecer la conexion
     */
    private Connection abrirConexion() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

}
