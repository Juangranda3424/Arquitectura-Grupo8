package ec.edu.monster.controller;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.model.MovimientoModel;
import ec.edu.monster.model.ResumenMovimientos;
import ec.edu.monster.servicio.EurekaBankSoapClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MovimientoServlet", urlPatterns = {"/Movimiento", "/Movimiento/Index"})
public class MovimientoServlet extends HttpServlet {

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
            EurekaBankSoapClient soapClient = new EurekaBankSoapClient();
            String numeroCuenta = cuenta.trim();

            List<MovimientoModel> movimientos = soapClient.consultarMovimientos(numeroCuenta);
            CuentaModel cuentaModel = soapClient.obtenerCuenta(numeroCuenta);

            double saldoActual = cuentaModel != null ? cuentaModel.getSaldo() : 0;

            request.setAttribute("cuentaConsultada", numeroCuenta);
            request.setAttribute("resumen", ResumenMovimientos.calcular(saldoActual, movimientos));
        } catch (RuntimeException ex) {
            request.setAttribute("error", ex.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/movimiento.jsp").forward(request, response);
    }
}
