package ec.edu.monster.modelo;

public final class Constantes {

    // Constructor privado para evitar que la clase sea instanciada
    private Constantes() {}

    // Cero absoluto expresado en grados Celsius
    public static final double CERO_ABSOLUTO_CELSIUS = -273.15;

    // Credenciales en memoria (SINBDD)
    public static final String USUARIO = "MONSTER";
    public static final String CONTRASENA = "MONSTER9";

    // Vigencia de los tokens de sesión, en minutos
    public static final long TOKEN_TTL_MINUTOS = 30;
}
