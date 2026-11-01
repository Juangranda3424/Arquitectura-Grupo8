package ec.edu.monster.controller;

import ec.edu.monster.model.RetiroRequest;
import ec.edu.monster.model.OperacionResponse;
import ec.edu.monster.servicio.RestApiClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "RetiroServlet", urlPatterns = {"/Retiro", "/Retiro/Index"})
public class RetiroServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/views/retiro.jsp").forward(request, response);
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

        if (cuenta == null || cuenta.trim().isEmpty() || 
            montoStr == null || montoStr.trim().isEmpty()) {
            request.setAttribute("error", "Por favor complete todos los campos");
            request.getRequestDispatcher("/WEB-INF/views/retiro.jsp").forward(request, response);
            return;
        }

        try {
            double monto = Double.parseDouble(montoStr);
            if (monto <= 0) {
                request.setAttribute("error", "El monto debe ser un número positivo válido");
                request.getRequestDispatcher("/WEB-INF/views/retiro.jsp").forward(request, response);
                return;
            }

            RestApiClient restClient = new RestApiClient();
            RetiroRequest retiroRequest = new RetiroRequest();
            retiroRequest.setCuenta(cuenta.trim());
            retiroRequest.setMonto(monto);

            OperacionResponse operacionResponse = restClient.post("cuenta/retiro", retiroRequest, OperacionResponse.class);

            if (operacionResponse != null && operacionResponse.isSuccess()) {
                request.setAttribute("success", operacionResponse.getMensaje() != null ? 
                    operacionResponse.getMensaje() : "Retiro realizado con éxito");
            } else {
                request.setAttribute("error", operacionResponse != null && operacionResponse.getMensaje() != null ?
                    operacionResponse.getMensaje() : "Error al realizar el retiro. Verifique los datos de la cuenta y el saldo disponible");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El monto debe ser un número válido");
        } catch (Exception ex) {
            request.setAttribute("error", "Error al conectar con el servidor: " + ex.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/retiro.jsp").forward(request, response);
    }
}




