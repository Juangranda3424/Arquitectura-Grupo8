package ec.edu.monster.cliente.rest;

/**
 * Error al invocar el servicio REST del servidor.
 *
 * <p>Mantiene la semántica de {@code SoapException} de TI.1.1 pero añade el código
 * ({@code codigo} del envelope o HTTP) para poder distinguir, por ejemplo, un 401.</p>
 */
public class RestException extends Exception {

    public enum Tipo {
        /** El servidor respondió con un envelope de error (validación, token inválido, 404...). */
        FALLO_SERVICIO,
        /** No se pudo establecer comunicación con el servidor. */
        SIN_CONEXION,
        /** El servidor respondió algo que no es un envelope JSON válido. */
        RESPUESTA_INVALIDA
    }

    private final Tipo tipo;
    private final int codigo;

    public RestException(Tipo tipo, String mensaje) {
        this(tipo, 0, mensaje, null);
    }

    public RestException(Tipo tipo, String mensaje, Throwable causa) {
        this(tipo, 0, mensaje, causa);
    }

    public RestException(Tipo tipo, int codigo, String mensaje) {
        this(tipo, codigo, mensaje, null);
    }

    public RestException(Tipo tipo, int codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.tipo = tipo;
        this.codigo = codigo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    /** Código HTTP o {@code codigo} del envelope; {@code 0} si no aplica. */
    public int getCodigo() {
        return codigo;
    }

    /** {@code true} cuando el servidor rechazó la petición por falta de token o token inválido. */
    public boolean esErrorDeAutenticacion() {
        return codigo == 401;
    }
}
