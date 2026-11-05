using WS_EurekaBank_Donet_GR8.dao;
using WS_EurekaBank_Donet_GR8.model;

namespace WS_EurekaBank_Donet_GR8.servicio;

public class MovimientoService
{
    private readonly MovimientoDAO _dao;

    public MovimientoService(IConfiguration configuration)
    {
        _dao = new MovimientoDAO(configuration);
    }

    public List<MovimientoModel> ObtenerMovimiento(string cuenta)
    {
        return _dao.ObtenerMovimientosPorCuenta(cuenta);
    }
}

