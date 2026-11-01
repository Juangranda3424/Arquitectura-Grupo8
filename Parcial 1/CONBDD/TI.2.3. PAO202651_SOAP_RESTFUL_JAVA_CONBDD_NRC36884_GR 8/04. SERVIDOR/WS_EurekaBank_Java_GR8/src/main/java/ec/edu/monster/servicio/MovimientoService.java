/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.servicio;

import ec.edu.monster.dao.MovimientoDAO;
import ec.edu.monster.modelo.MovimientoModel;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author crist
 */
public class MovimientoService {
    MovimientoDAO DAO = new MovimientoDAO();

    public List<MovimientoModel> ObtenerMovimiento(String cuenta) throws SQLException {
        return DAO.obtenerMovimientosPorCuenta(cuenta);
    }
}
