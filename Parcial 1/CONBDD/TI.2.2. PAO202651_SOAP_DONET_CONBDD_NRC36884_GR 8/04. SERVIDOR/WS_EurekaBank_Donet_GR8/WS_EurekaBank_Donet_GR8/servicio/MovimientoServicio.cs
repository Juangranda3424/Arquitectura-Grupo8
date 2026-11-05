using System.Collections.Generic;
using WS_EurekaBank_Donet_GR8.dao;
using WS_EurekaBank_Donet_GR8.modelo;

namespace WS_EurekaBank_Donet_GR8.servicio
{
    /// <summary>
    /// Consulta de movimientos de una cuenta.
    /// </summary>
    public class MovimientoServicio
    {
        private readonly MovimientoDAO dao = new MovimientoDAO();

        public List<MovimientoModel> ObtenerMovimientos(string cuenta)
        {
            return dao.ObtenerMovimientosPorCuenta(cuenta);
        }
    }
}
