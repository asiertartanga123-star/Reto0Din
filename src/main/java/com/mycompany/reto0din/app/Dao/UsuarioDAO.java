package com.mycompany.reto0din.app.Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    private static final String DB_URL = System.getenv().getOrDefault(
        "DB_URL", "jdbc:mysql://localhost:3306/tolodb");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "abcd*1234");

    // Devuelve el tipo (ADMIN, CLIENTE, EMPLEADO) o null si el email/contraseña no son correctos
    public String autenticar(String email, String password) throws SQLException {
        String sql = "SELECT tipo FROM usuario WHERE mail = ? AND contrasenia = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String tipo = result.getString("tipo");
                    return tipo == null ? null : tipo.trim().toUpperCase();
                }
                return null;
            }
        }
    }
}