package ec.edu.monster.climov.modelo;

/**
 * Configuracion del servicio SOAP de TI.2.2 (servidor WCF WS_EurekaBank_Donet_GR8).
 * 10.0.2.2 es el host del emulador Android (localhost de la maquina).
 */
public final class ConfiguracionSoap {

    public static final String ESPACIO_NOMBRES = "http://ws.monster.edu.ec/";
    public static final String URL_SERVICIO =
            "http://10.0.2.2:8090/WS_EurekaBank_Donet_GR8/webservicio/WSEureka.svc";

    public static final String OPERACION_LOGIN = "login";
    public static final String OPERACION_REG_DEPOSITO = "regDeposito";
    public static final String OPERACION_TRAER_MOVIMIENTOS = "traerMovimientos";

    public static final String TIPO_DEPOSITO = "003";
    public static final String TIPO_RETIRO = "004";
    public static final String TIPO_TRANSFERENCIA = "008";

    private ConfiguracionSoap() {
    }

    public static String accion(String operacion) {
        return ESPACIO_NOMBRES + "WSEureka/" + operacion;
    }
}
