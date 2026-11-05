using System;
using System.Collections.Generic;
using System.Globalization;
using MySql.Data.MySqlClient;
using WS_EurekaBank_Donet_GR8.db;
using WS_EurekaBank_Donet_GR8.modelo;

namespace WS_EurekaBank_Donet_GR8.dao
{
    /// <summary>
    /// Operaciones sobre las tablas movimiento y tipomovimiento.
    /// </summary>
    public class MovimientoDAO
    {
        /// <summary>
        /// Movimientos de una cuenta con el saldo acumulado, ordenados del mas reciente al mas antiguo.
        /// </summary>
        public List<MovimientoModel> ObtenerMovimientosPorCuenta(string codigoCuenta)
        {
            const string sql = "SELECT m.chr_cuencodigo, m.int_movinumero, m.dtt_movifecha, "
                + "m.chr_emplcodigo, m.chr_tipocodigo, m.dec_moviimporte, m.chr_cuenreferencia, "
                + "t.vch_tipodescripcion AS tipoDescripcion, t.vch_tipoaccion AS tipoAccion "
                + "FROM movimiento AS m "
                + "INNER JOIN tipomovimiento AS t ON t.chr_tipocodigo = m.chr_tipocodigo "
                + "WHERE m.chr_cuencodigo = @cuenta "
                + "ORDER BY m.dtt_movifecha ASC, m.int_movinumero ASC";

            List<MovimientoModel> movimientos = new List<MovimientoModel>();
            double saldoAcumulado = 0.0;

            using (MySqlConnection conexion = ConexionBD.ObtenerConexion())
            using (MySqlCommand comando = new MySqlCommand(sql, conexion))
            {
                comando.Parameters.AddWithValue("@cuenta", codigoCuenta);

                using (MySqlDataReader lector = comando.ExecuteReader())
                {
                    while (lector.Read())
                    {
                        MovimientoModel movimiento = new MovimientoModel();
                        movimiento.CodigoCuenta = lector.GetString("chr_cuencodigo");
                        movimiento.NumeroMovimiento = lector.GetInt32("int_movinumero");
                        movimiento.FechaMovimiento = lector.GetDateTime("dtt_movifecha").ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
                        movimiento.CodigoEmpleado = lector.GetString("chr_emplcodigo");
                        movimiento.CodigoTipoMovimiento = lector.GetString("chr_tipocodigo");
                        movimiento.TipoDescripcion = lector.GetString("tipoDescripcion");
                        movimiento.TipoAccion = lector.GetString("tipoAccion");
                        movimiento.ImporteMovimiento = lector.GetDouble("dec_moviimporte");
                        movimiento.CuentaReferencia = lector.IsDBNull(lector.GetOrdinal("chr_cuenreferencia"))
                            ? null
                            : lector.GetString("chr_cuenreferencia");

                        // Saldo acumulado segun la accion del tipo de movimiento
                        if (string.Equals(movimiento.TipoAccion, "INGRESO", StringComparison.OrdinalIgnoreCase))
                        {
                            saldoAcumulado += movimiento.ImporteMovimiento;
                        }
                        else if (string.Equals(movimiento.TipoAccion, "SALIDA", StringComparison.OrdinalIgnoreCase))
                        {
                            saldoAcumulado -= movimiento.ImporteMovimiento;
                        }

                        movimiento.Saldo = saldoAcumulado;
                        movimientos.Add(movimiento);
                    }
                }
            }

            // Mas reciente primero: por fecha descendente y luego por numero de movimiento descendente
            movimientos.Sort(delegate (MovimientoModel m1, MovimientoModel m2)
            {
                int comparacionFecha = string.CompareOrdinal(m2.FechaMovimiento, m1.FechaMovimiento);
                if (comparacionFecha != 0)
                {
                    return comparacionFecha;
                }

                return m2.NumeroMovimiento.CompareTo(m1.NumeroMovimiento);
            });

            return movimientos;
        }

        public void RegistrarMovimiento(MovimientoModel movimiento)
        {
            const string sql = "INSERT INTO movimiento (chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, "
                + "chr_tipocodigo, dec_moviimporte, chr_cuenreferencia) "
                + "VALUES (@cuenta, @numero, @fecha, @empleado, @tipo, @importe, @referencia)";

            using (MySqlConnection conexion = ConexionBD.ObtenerConexion())
            using (MySqlCommand comando = new MySqlCommand(sql, conexion))
            {
                comando.Parameters.AddWithValue("@cuenta", movimiento.CodigoCuenta);
                comando.Parameters.AddWithValue("@numero", movimiento.NumeroMovimiento);
                comando.Parameters.AddWithValue("@fecha", DateTime.ParseExact(movimiento.FechaMovimiento, "yyyy-MM-dd", CultureInfo.InvariantCulture));
                comando.Parameters.AddWithValue("@empleado", movimiento.CodigoEmpleado);
                comando.Parameters.AddWithValue("@tipo", movimiento.CodigoTipoMovimiento);
                comando.Parameters.AddWithValue("@importe", movimiento.ImporteMovimiento);
                comando.Parameters.AddWithValue("@referencia", (object)movimiento.CuentaReferencia ?? DBNull.Value);
                comando.ExecuteNonQuery();
            }
        }
    }
}
