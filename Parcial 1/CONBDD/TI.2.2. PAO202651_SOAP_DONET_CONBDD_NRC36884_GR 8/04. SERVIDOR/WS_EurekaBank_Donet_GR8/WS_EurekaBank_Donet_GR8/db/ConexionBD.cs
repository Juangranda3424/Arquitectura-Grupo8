using System;

namespace WS_EurekaBank_Donet_GR8.ec.edu.monster.db
{
    /// <summary>
    /// Acceso a la base de datos MySQL del esquema eurekabank.
    /// </summary>
    public static class ConexionBD
    {
        private const string NombreCadenaConexion = "EurekaBank";

        private const string CadenaPorDefecto =
            "Server=127.0.0.1;Port=3306;Database=eurekabank_ti22;Uid=MONSTER;Pwd=MONSTER9;SslMode=None;AllowPublicKeyRetrieval=True;CharSet=utf8mb4;";

        public static string CadenaConexion
        {
            get
            {
                System.Configuration.ConnectionStringSettings configuracion =
                    System.Configuration.ConfigurationManager.ConnectionStrings[NombreCadenaConexion];

                return configuracion != null && !string.IsNullOrWhiteSpace(configuracion.ConnectionString)
                    ? configuracion.ConnectionString
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
