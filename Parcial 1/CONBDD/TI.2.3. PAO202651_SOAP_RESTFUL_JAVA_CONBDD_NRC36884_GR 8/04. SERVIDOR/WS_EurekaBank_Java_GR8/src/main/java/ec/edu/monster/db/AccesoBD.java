/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author crist
 */
public class AccesoBD {
    private static final String URL =
        "jdbc:mysql://127.0.0.1:3306/eurekabank_ti23"
        + "?useSSL=false"
        + "&serverTimezone=UTC"
        + "&allowPublicKeyRetrieval=true"; // útil con MySQL 8 y cuentas nuevas

    private static final String USER = "MONSTER";     // o "MONSTER"
    private static final String PASS = "MONSTER9"; // o "MONSTER9"

     public static Connection getConnection() throws SQLException {
        try {
            // Con JDBC 4+ no es obligatorio, pero mantiene compatibilidad
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver MySQL (com.mysql.cj.jdbc.Driver).", e);
        } catch (SQLException e) {
            // re-lanzamos con contexto
            throw new SQLException("Error al conectar a MySQL: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("Error inesperado al conectar.", e);
        }
    }
     public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
