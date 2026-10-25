package ec.edu.monster.modelo;

public class Usuario {

    private String usuario;
    private String contrasena;

    public Usuario() {
    }

    public Usuario(String username, String password) {
        this.usuario = username;
        this.contrasena = password;
    }

    public String getUsername() {
        return usuario;
    }

    public void setUsername(String username) {
        this.usuario = username;
    }

    public String getPassword() {
        return contrasena;
    }

    public void setPassword(String password) {
        this.contrasena = password;
    }
}
