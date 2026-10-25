package ec.edu.monster.controlador;

import ec.edu.monster.servicio.ClienteREST;
import ec.edu.monster.servicio.RespuestaAPI;

import jakarta.json.JsonObject;

/**
 * Controlador de conversiones contra el servicio REST.
 *
 * <p>Conserva la firma pública de TI.1.1
 * ({@code ejecutarConversion(valor, origen, destino, magnitud)}): devuelve el número calculado por
 * el servidor o propaga el {@code mensaje} de validación tal cual lo envía la API.</p>
 */
public class ConversorUnidades {

    private final ClienteREST cliente;

    public ConversorUnidades() {
        this(new ClienteREST());
    }

    public ConversorUnidades(ClienteREST cliente) {
        this.cliente = cliente;
    }

    /**
     * {@code POST /api/conversiones/{magnitud}}
     *
     * @return el valor convertido (sin redondear, tal como lo devuelve el servidor)
     * @throws Exception si el servidor rechaza la conversión (mensaje del servidor) o no hay
     *                   conectividad
     */
    public double ejecutarConversion(Double valorOrigen, String unidadOrigen, String unidadDestino,
                                     String tipoMagnitud) throws Exception {
        double valor = valorOrigen == null ? 0.0 : valorOrigen;

        RespuestaAPI respuesta = cliente.convertir(tipoMagnitud, valor, unidadOrigen, unidadDestino);

        if (!respuesta.esExitosa()) {
            throw new Exception(respuesta.getMensaje());
        }

        JsonObject resultado = respuesta.getResultado();
        if (resultado == null || !resultado.containsKey("valorResultado")) {
            throw new Exception("El servidor no devolvió un resultado de conversión.");
        }

        return resultado.getJsonNumber("valorResultado").doubleValue();
    }
}
