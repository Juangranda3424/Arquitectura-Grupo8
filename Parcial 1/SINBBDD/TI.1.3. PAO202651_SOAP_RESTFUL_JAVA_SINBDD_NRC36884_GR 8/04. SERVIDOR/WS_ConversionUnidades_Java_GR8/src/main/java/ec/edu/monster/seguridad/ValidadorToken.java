package ec.edu.monster.seguridad;

import ec.edu.monster.api.soporte.ErrorAPI;
import ec.edu.monster.servicio.TokenServicio;

/**
 * Extrae y valida el token de sesión del encabezado HTTP {@code Authorization: Bearer <token>}.
 *
 * <p>A diferencia de TI.1.1 (donde el token viajaba en la cabecera SOAP), aquí la capa REST lo
 * lee de la petición HTTP; el esquema {@code Bearer} no distingue mayúsculas.</p>
 */
public final class ValidadorToken {

    /** Mensaje de paridad para la ausencia del encabezado. */
    public static final String MENSAJE_FALTA_BEARER =
            "Acceso denegado: falta el encabezado Authorization con el esquema Bearer.";

    /** Mensaje de paridad para un token desconocido o vencido. */
    public static final String MENSAJE_TOKEN_INVALIDO =
            "Acceso denegado: Token inválido o sesión expirada.";

    private static final String ESQUEMA_BEARER = "Bearer ";

    private ValidadorToken() {
    }

    /**
     * @param authorizationHeader valor del encabezado {@code Authorization}
     * @return el token que sigue al esquema {@code Bearer}, o {@code null} si el encabezado no
     *         trae un token
     */
    public static String extraerToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }
        String cabecera = authorizationHeader.trim();
        if (cabecera.length() <= ESQUEMA_BEARER.length()
                || !cabecera.regionMatches(true, 0, ESQUEMA_BEARER, 0, ESQUEMA_BEARER.length())) {
            return null;
        }
        String token = cabecera.substring(ESQUEMA_BEARER.length()).trim();
        return token.isEmpty() ? null : token;
    }

    /**
     * Exige un token vigente en el encabezado.
     *
     * @throws ErrorAPI 401 cuando falta el encabezado o cuando el token es desconocido o venció
     */
    public static void exigirTokenValido(String authorizationHeader, TokenServicio tokens) {
        String token = extraerToken(authorizationHeader);
        if (token == null) {
            throw new ErrorAPI(401, MENSAJE_FALTA_BEARER);
        }
        if (tokens == null || !tokens.validar(token)) {
            throw new ErrorAPI(401, MENSAJE_TOKEN_INVALIDO);
        }
    }
}
