package ec.edu.monster.cliente.soap;

/** Error al invocar el servicio SOAP. */
public class SoapException extends Exception {

    public enum Tipo {
        /** El servidor respondió con un SOAP Fault (validación, token inválido, etc.). */
        FALLO_SERVICIO,
        /** No se pudo establecer comunicación con el servidor. */
        SIN_CONEXION,
        /** El servidor respondió algo que no es una respuesta SOAP válida. */
        RESPUESTA_INVALIDA
    }

    private final Tipo tipo;

    public SoapException(Tipo tipo, String mensaje) {
        super(mensaje);
        this.tipo = tipo;
    }

    public SoapException(Tipo tipo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.tipo = tipo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    /** El handler ValidadorToken del servidor rechaza con mensajes "Acceso denegado: ...". */
    public boolean esErrorDeAutenticacion() {
        return tipo == Tipo.FALLO_SERVICIO
                && getMessage() != null
                && getMessage().startsWith("Acceso denegado");
    }
}
