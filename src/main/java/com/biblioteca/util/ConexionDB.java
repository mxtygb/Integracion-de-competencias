package com.biblioteca.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // ── Ajusta estos valores según tu entorno ──────────────────────────────────
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "matias006";
    // ──────────────────────────────────────────────────────────────────────────

    private ConexionDB() {}

    /**
     * Crea y devuelve una nueva conexión cada vez.
     * Úsala siempre dentro de un try-with-resources para cerrarla automáticamente.
     *
     * Ejemplo:
     *   try (Connection conn = ConexionDB.getConexion()) { ... }
     */
    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
