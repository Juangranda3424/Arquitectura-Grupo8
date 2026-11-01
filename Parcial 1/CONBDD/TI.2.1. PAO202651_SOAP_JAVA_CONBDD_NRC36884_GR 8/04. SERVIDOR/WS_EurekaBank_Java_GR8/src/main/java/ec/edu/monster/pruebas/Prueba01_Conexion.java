/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.pruebas;

import ec.edu.monster.db.AccesoBD;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Prueba01_Conexion {

    public static void main(String[] args) {
        System.out.println("Iniciando prueba de conexión a MySQL...");
        try (Connection cn = AccesoBD.getConnection()) {
            // Si llegó aquí, la conexión se abrió correctamente
            DatabaseMetaData md = cn.getMetaData();
            System.out.println("✅ Conectado.");
            System.out.println("Driver: " + md.getDriverName() + " " + md.getDriverVersion());
            System.out.println("Servidor: " + md.getDatabaseProductName() + " " + md.getDatabaseProductVersion());

            // Prueba rápida: SELECT 1
            try (Statement st = cn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT DATABASE() AS db, 1 AS ok")) {
                if (rs.next()) {
                    System.out.println("Base actual: " + rs.getString("db"));
                    System.out.println("Ping SELECT 1: " + rs.getInt("ok"));
                }
            }

            // Prueba extra: contar tablas en eurekabank
            try (Statement st = cn.createStatement();
                 ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*) AS tablas " +
                    "FROM information_schema.tables " +
                    "WHERE table_schema = 'eurekabank_ti21'")) {
                if (rs.next()) {
                    System.out.println("Tablas en 'eurekabank_ti21': " + rs.getInt("tablas"));
                }
            }

            System.out.println("🎉 Conexión y consultas OK.");
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión/SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
