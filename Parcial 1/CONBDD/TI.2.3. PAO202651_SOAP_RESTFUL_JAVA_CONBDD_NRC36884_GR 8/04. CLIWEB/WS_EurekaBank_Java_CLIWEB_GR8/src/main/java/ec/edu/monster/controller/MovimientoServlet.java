package ec.edu.monster.controller;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.model.MovimientoModel;
import ec.edu.monster.model.MovimientoRequest;
import ec.edu.monster.servicio.RestApiClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "MovimientoServlet", urlPatterns = {"/Movimiento", "/Movimiento/Index"})
public class MovimientoServlet extends HttpServlet {

    // Códigos de tipo de movimiento
    private static final Set<String> COD_EGRESO = new HashSet<String>() {{
        add("004"); // Retiro
    }};
    
    private static final Set<String> COD_INGRESO = new HashSet<String>() {{
        add("001"); // Apertura
        add("003"); // Depósito
        add("009"); // Transferencia
    }};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/views/movimiento.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String cuenta = request.getParameter("cuenta");

        if (cuenta == null || cuenta.trim().isEmpty()) {
            request.setAttribute("error", "Por favor ingrese el número de cuenta");
            request.getRequestDispatcher("/WEB-INF/views/movimiento.jsp").forward(request, response);
            return;
        }

        try {
            RestApiClient restClient = new RestApiClient();

            // Obtener movimientos
            MovimientoRequest movimientoRequest = new MovimientoRequest();
            movimientoRequest.setNumcuenta(cuenta.trim());

            List<MovimientoModel> movimientos = restClient.postMovimientos("movimiento", movimientoRequest);

            // Obtener datos de la cuenta
            CuentaModel cuentaModel = restClient.get("cuenta/" + cuenta.trim(), CuentaModel.class);

            double saldoActual = cuentaModel != null ? cuentaModel.getDecCuenSaldo() : 0;
            double totalIngresos = 0;
            double totalEgresos = 0;

            // Calcular totales
            if (movimientos != null) {
                for (MovimientoModel mov : movimientos) {
                    if (mov == null) continue;

                    double importe = mov.getImporteMovimiento();
                    String codigo = mov.getCodigoTipoMovimiento() != null ? mov.getCodigoTipoMovimiento() : "";
                    String descripcion = mov.getTipoDescripcion() != null ? mov.getTipoDescripcion().toLowerCase().trim() : "";

                    if (COD_EGRESO.contains(codigo) || descripcion.equals("retiro")) {
                        totalEgresos += importe;
                    } else if (COD_INGRESO.contains(codigo) || descripcion.equals("deposito") || 
                               descripcion.equals("depósito") || descripcion.equals("transferencia") || 
                               descripcion.equals("apertura de cuenta")) {
                        totalIngresos += importe;
                    }
                }
            }

            request.setAttribute("movimientos", movimientos);
            request.setAttribute("saldoActual", saldoActual);
            request.setAttribute("totalIngresos", totalIngresos);
            request.setAttribute("totalEgresos", totalEgresos);
            request.setAttribute("saldoNeto", totalIngresos - totalEgresos);
        } catch (Exception ex) {
            request.setAttribute("error", "Error al conectar con el servidor: " + ex.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/movimiento.jsp").forward(request, response);
    }
}




