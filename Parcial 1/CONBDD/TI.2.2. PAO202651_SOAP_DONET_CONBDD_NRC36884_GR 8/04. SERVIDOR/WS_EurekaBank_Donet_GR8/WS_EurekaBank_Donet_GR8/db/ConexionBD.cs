using System;

namespace WS_EurekaBank_Donet_GR8.db
{
    /// <summary>
    /// Acceso a la base de datos MySQL del esquema eurekabank.
    /// </summary>
    public static class ConexionBD
    {
        private const string CadenaPorDefecto =
            "Server=127.0.0.1;Port=3306;Database=eurekabank_ti22;Uid=MONSTER;Pwd=MONSTER9;SslMode=None;AllowPublicKeyRetrieval=True;CharSet=utf8mb4;";

        private static string cadenaConfigurada;

        /// <summary>
        /// Recibe la cadena de appsettings.json (ConnectionStrings:EurekaBank) al iniciar el servidor.
        /// </summary>
        public static void Configurar(string cadenaConexion)
        {
            cadenaConfigurada = cadenaConexion;
        }

        public static string CadenaConexion
        {
            get
            {
                return !string.IsNullOrWhiteSpace(cadenaConfigurada)
                    ? cadenaConfigurada
                    : CadenaPorDefecto;
            }
        }

        public static MySql.Data.MySqlClient.MySqlConnection ObtenerConexion()
        {
            MySql.Data.MySqlClient.MySqlConnection conexion =
                new MySql.Data.MySqlClient.MySqlConnection(CadenaConexion);
            conexion.Open();
            return conexion;
        }
    }
}
