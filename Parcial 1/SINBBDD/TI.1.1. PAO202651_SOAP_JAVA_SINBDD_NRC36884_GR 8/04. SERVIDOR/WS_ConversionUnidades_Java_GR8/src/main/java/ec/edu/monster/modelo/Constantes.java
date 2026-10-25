package ec.edu.monster.modelo;

import java.util.HashSet;
import java.util.Set;

public final class Constantes {

    // Constructor privado para evitar que la clase sea instanciada
    private Constantes() {}

    // Cero absoluto expresado en grados Celsius
    public static final double CERO_ABSOLUTO_CELSIUS = -273.15;
    
    // Credenciales
    public static final String usuario = "MONSTER";
    public static final String contrasena = "MONSTER9";
    
    // Lista de Tokens Activos
    public static final Set<String> tokensActivos = new HashSet<>();
    
    // El namespace
    public static final String NAMESPACE = "http://localhost:8080/";

    
}