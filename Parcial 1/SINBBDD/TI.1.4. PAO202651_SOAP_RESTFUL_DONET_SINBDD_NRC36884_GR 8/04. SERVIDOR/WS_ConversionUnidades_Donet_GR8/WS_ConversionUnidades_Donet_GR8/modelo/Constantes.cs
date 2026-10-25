using System.Collections.Concurrent;

namespace WS_ConversionUnidades_Donet_GR8.modelo
{
    public static class Constantes
    {
        // Cero absoluto expresado en grados Celsius
        public const double CERO_ABSOLUTO_CELSIUS = -273.15;

        // Credenciales en memoria (SINBDD)
        public const string usuario = "MONSTER";
        public const string contrasena = "MONSTER9";

        // Registro de tokens activos compartido por todo el servidor.
        // Reemplaza al HashSet de TI.1.2 por una colección segura ante peticiones concurrentes de Kestrel.
        public static readonly ConcurrentDictionary<string, byte> tokensActivos = new ConcurrentDictionary<string, byte>();

        // Identificación pública del servicio
        public const string NOMBRE_SERVICIO = "WS_ConversionUnidades_Donet_GR8";
        public const string VERSION_SERVICIO = "1.0.0";
    }
}
