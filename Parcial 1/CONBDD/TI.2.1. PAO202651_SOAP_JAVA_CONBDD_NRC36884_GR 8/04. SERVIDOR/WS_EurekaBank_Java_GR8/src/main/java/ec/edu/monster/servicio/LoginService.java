package ec.edu.monster.servicio;

import ec.edu.monster.db.AccesoBD;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginService {

    private static final String SQL =
            "SELECT usuario, contrasena FROM credenciales WHERE usuario = ?";

    /**
     * Validates the credentials against the credenciales table, where the password is stored
     * as an MD5 hex hash. The user name comparison is case-sensitive.
     */
    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        String hash = md5(password);
        try (Connection cn = AccesoBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (username.equals(rs.getString("usuario"))
                            && hash.equalsIgnoreCase(rs.getString("contrasena"))) {
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo validar las credenciales: " + e.getMessage(), e);
        }
    }

    private static String md5(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 no disponible", e);
        }
    }
}
