using System;
using System.Data;
using MySql.Data.MySqlClient;
using WS_EurekaBank_Donet_GR8.db;
using WS_EurekaBank_Donet_GR8.modelo;

namespace WS_EurekaBank_Donet_GR8.dao
{
    /// <summary>
    /// Operaciones sobre la tabla cuenta.
    /// </summary>
    public class CuentaDAO
    {
        /// <summary>
        /// Actualiza el saldo de la cuenta cuando se registra un movimiento
        /// (retiro, deposito o transferencia) e incrementa el contador de movimientos.
        /// </summary>
        public bool ActualizarSaldoCuenta(string codigoCuenta, string valorMovimiento)
        {
            const string sql = "UPDATE cuenta "
                + "SET dec_cuensaldo = dec_cuensaldo + @valor, "
                + "int_cuencontmov = int_cuencontmov + 1 "
                + "WHERE chr_cuencodigo = @cuenta";

            using (MySqlConnection conexion = ConexionBD.ObtenerConexion())
            using (MySqlCommand comando = new MySqlCommand(sql, conexion))
            {
                comando.Parameters.AddWithValue("@valor", double.Parse(valorMovimiento, System.Globalization.CultureInfo.InvariantCulture));
                comando.Parameters.AddWithValue("@cuenta", codigoCuenta);

                int filasActualizadas = comando.ExecuteNonQuery();
                return filasActualizadas > 0;
            }
        }

        public Cuenta ObtenerCuentaPorCodigo(string codigoCuenta)
        {
            const string sql = "SELECT chr_cuencodigo, chr_monecodigo, chr_sucucodigo, chr_emplcreacuenta, "
                + "chr_cliecodigo, dec_cuensaldo, dtt_cuenfechacreacion, vch_cuenestado, int_cuencontmov, chr_cuenclave "
                + "FROM cuenta WHERE chr_cuencodigo = @cuenta";

            using (MySqlConnection conexion = ConexionBD.ObtenerConexion())
            using (MySqlCommand comando = new MySqlCommand(sql, conexion))
            {
                comando.Parameters.AddWithValue("@cuenta", codigoCuenta);

                using (MySqlDataReader lector = comando.ExecuteReader(CommandBehavior.SingleRow))
                {
                    if (!lector.Read())
                    {
                        return null;
                    }

                    Cuenta cuenta = new Cuenta();
                    cuenta.ChrCuenCodigo = lector.GetString("chr_cuencodigo");
                    cuenta.ChrMoneCodigo = lector.GetString("chr_monecodigo");
                    cuenta.ChrSucucodigo = lector.GetString("chr_sucucodigo");
                    cuenta.ChrEmplCreaCuenta = lector.GetString("chr_emplcreacuenta");
                    cuenta.ChrClieCodigo = lector.GetString("chr_cliecodigo");
                    cuenta.DecCuenSaldo = lector.GetDouble("dec_cuensaldo");
                    cuenta.DttCuenFechaCreacion = lector.GetDateTime("dtt_cuenfechacreacion");
                    cuenta.VchCuenEstado = lector.GetString("vch_cuenestado");
                    cuenta.IntCuenContMov = lector.GetInt32("int_cuencontmov");
                    cuenta.ChrCuenClave = lector.GetString("chr_cuenclave");
                    return cuenta;
                }
            }
        }
    }
}
