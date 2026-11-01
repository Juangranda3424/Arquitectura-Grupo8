package ec.edu.monster.controller;

import ec.edu.monster.servicio.EurekaBankSoapClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "TransferenciaServlet", urlPatterns = {"/Transferencia", "/Transferencia/Index"})
public class TransferenciaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/transferencia.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String cuentaOrigen = request.getParameter("cuentaOrigen");
        String cuentaDestino = request.getParameter("cuentaDestino");
        String montoStr = request.getParameter("monto");

        if (cuentaOrigen == null || cuentaOrigen.trim().isEmpty()
                || cuentaDestino == null || cuentaDestino.trim().isEmpty()
                || montoStr == null || montoStr.trim().isEmpty()) {
            request.setAttribute("error", "Por favor complete todos los campos");
            request.getRequestDispatcher("/WEB-INF/views/transferencia.jsp").forward(request, response);
            return;
        }

        if (cuentaOrigen.trim().equals(cuentaDestino.trim())) {
            request.setAttribute("error", "La cuenta origen y destino no pueden ser la misma");
            request.getRequestDispatcher("/WEB-INF/views/transferencia.jsp").forward(request, response);
            return;
        }

        try {
            double monto = Double.parseDouble(montoStr.trim());
            if (monto <= 0) {
                request.setAttribute("error", "El monto debe ser un número positivo válido");
                request.getRequestDispatcher("/WEB-INF/views/transferencia.jsp").forward(request, response);
                return;
            }

            EurekaBankSoapClient soapClient = new EurekaBankSoapClient();
            boolean registrada = soapClient.registrarOperacion(
                    cuentaOrigen.trim(), String.valueOf(monto), "TRA", cuentaDestino.trim());

            if (registrada) {
                request.setAttribute("success", "Transferencia realizada con éxito");
            } else {
                request.setAttribute("error",
                        "Error al realizar la transferencia. Verifique los datos de las cuentas"
                                + " y el saldo disponible en la cuenta origen");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El monto debe ser un número válido");
        } catch (RuntimeException ex) {
            request.setAttribute("error", ex.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/transferencia.jsp").forward(request, response);
    }
}
