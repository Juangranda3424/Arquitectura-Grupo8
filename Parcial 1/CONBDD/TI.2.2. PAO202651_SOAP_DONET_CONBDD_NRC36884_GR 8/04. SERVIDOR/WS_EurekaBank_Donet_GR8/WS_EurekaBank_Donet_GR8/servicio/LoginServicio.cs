using System;
using System.Security.Cryptography;
using System.Text;
using MySql.Data.MySqlClient;
using WS_EurekaBank_Donet_GR8.db;

namespace WS_EurekaBank_Donet_GR8.servicio
{
    /// <summary>
    /// Autenticacion de los clientes del banco contra la tabla credenciales,
    /// donde la contrasena se guarda como hash MD5 en hexadecimal.
    /// </summary>
    public class LoginServicio
    {
        private const string Sql =
            "SELECT usuario, contrasena FROM credenciales WHERE usuario = @usuario";

        public bool Login(string usuario, string contrasena)
        {
            if (usuario == null || contrasena == null)
            {
                return false;
            }

            string hash = Md5(contrasena);

            using (MySqlConnection conexion = ConexionBD.ObtenerConexion())
            using (MySqlCommand comando = new MySqlCommand(Sql, conexion))
            {
                comando.Parameters.AddWithValue("@usuario", usuario);
                using (MySqlDataReader lector = comando.ExecuteReader())
                {
                    while (lector.Read())
                    {
                        // Case-sensitive user name, like the previous hardcoded check.
                        if (string.Equals(usuario, lector.GetString("usuario"), StringComparison.Ordinal)
                            && string.Equals(hash, lector.GetString("contrasena"), StringComparison.OrdinalIgnoreCase))
                        {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        private static string Md5(string valor)
        {
            using (MD5 md5 = MD5.Create())
            {
                byte[] bytes = md5.ComputeHash(Encoding.UTF8.GetBytes(valor));
                return Convert.ToHexString(bytes).ToLowerInvariant();
            }
        }
    }
}
