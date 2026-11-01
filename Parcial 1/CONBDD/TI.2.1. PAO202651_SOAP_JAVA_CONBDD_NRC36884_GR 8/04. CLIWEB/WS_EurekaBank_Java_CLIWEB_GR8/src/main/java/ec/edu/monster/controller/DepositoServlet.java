package ec.edu.monster.controller;

import ec.edu.monster.servicio.EurekaBankSoapClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "DepositoServlet", urlPatterns = {"/Deposito", "/Deposito/Index"})
public class DepositoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/deposito.jsp").forward(request, response);
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
        String montoStr = request.getParameter("monto");

        if (cuenta == null || cuenta.trim().isEmpty()
                || montoStr == null || montoStr.trim().isEmpty()) {
            request.setAttribute("error", "Por favor complete todos los campos");
            request.getRequestDispatcher("/WEB-INF/views/deposito.jsp").forward(request, response);
            return;
        }

        try {
            double monto = Double.parseDouble(montoStr.trim());
            if (monto <= 0) {
                request.setAttribute("error", "El monto debe ser un número positivo válido");
                request.getRequestDispatcher("/WEB-INF/views/deposito.jsp").forward(request, response);
                return;
            }

            EurekaBankSoapClient soapClient = new EurekaBankSoapClient();
            boolean registrado = soapClient.registrarOperacion(cuenta.trim(), String.valueOf(monto), "DEP", null);

            if (registrado) {
                request.setAttribute("success", "Depósito realizado con éxito");
            } else {
                request.setAttribute("error", "Error al realizar el depósito. Verifique los datos de la cuenta");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El monto debe ser un número válido");
        } catch (RuntimeException ex) {
            request.setAttribute("error", ex.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/deposito.jsp").forward(request, response);
    }
}
