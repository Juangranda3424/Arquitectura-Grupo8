package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.Usuario;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro de tokens de sesión en memoria (SINBDD).
 *
 * <p>Igual que en TI.1.1 el registro es único para todo el servidor: cualquier instancia de
 * {@code TokenServicio} observa los mismos tokens, de modo que el login y la validación por
 * petición comparten estado. El {@code ConcurrentHashMap} reemplaza al {@code HashSet} estático
 * de TI.1.1 y agrega vencimiento configurable (por defecto {@link Constantes#TOKEN_TTL_MINUTOS}).</p>
 */
public class TokenServicio {

    private static final ConcurrentHashMap<String, Instant> TOKENS_ACTIVOS = new ConcurrentHashMap<>();

    private final long ttlMinutos;
    private final Clock reloj;

    public TokenServicio() {
        this(Constantes.TOKEN_TTL_MINUTOS);
    }

    public TokenServicio(long ttlMinutos) {
        this(ttlMinutos, Clock.systemUTC());
    }

    public TokenServicio(long ttlMinutos, Clock reloj) {
        this.ttlMinutos = ttlMinutos;
        this.reloj = reloj == null ? Clock.systemUTC() : reloj;
    }

    /**
     * Genera un token nuevo con el formato {@code UUID + usuario} de TI.1.1.
     */
    public String generarToken(Usuario usuario) {
        String token = UUID.randomUUID().toString() + usuario.getUsername();
        TOKENS_ACTIVOS.put(token, reloj.instant().plus(ttlMinutos, ChronoUnit.MINUTES));
        return token;
    }

    /**
     * @return {@code true} solo si el token existe y aún no venció; un token vencido
     *         se elimina del registro durante esta validación.
     */
    public boolean validar(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Instant vencimiento = TOKENS_ACTIVOS.get(token);
        if (vencimiento == null) {
            return false;
        }
        if (!reloj.instant().isBefore(vencimiento)) {
            TOKENS_ACTIVOS.remove(token);
            return false;
        }
        return true;
    }

    /**
     * @return {@code true} si el token estaba registrado y fue eliminado.
     */
    public boolean invalidar(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return TOKENS_ACTIVOS.remove(token) != null;
    }

    /**
     * @return instante de vencimiento registrado, o {@code null} si el token no existe.
     */
    public Instant vencimientoDe(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return TOKENS_ACTIVOS.get(token);
    }
}
