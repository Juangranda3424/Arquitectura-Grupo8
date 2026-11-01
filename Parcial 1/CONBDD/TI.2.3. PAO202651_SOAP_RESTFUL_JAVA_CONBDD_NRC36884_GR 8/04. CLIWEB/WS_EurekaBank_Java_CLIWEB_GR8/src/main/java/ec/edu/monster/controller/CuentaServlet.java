package ec.edu.monster.controller;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.servicio.RestApiClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "CuentaServlet", urlPatterns = {"/Cuenta", "/Cuenta/Index"})
public class CuentaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/views/cuenta.jsp").forward(request, response);
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
            request.getRequestDispatcher("/WEB-INF/views/cuenta.jsp").forward(request, response);
            return;
        }

        try {
            RestApiClient restClient = new RestApiClient();
            CuentaModel cuentaModel = restClient.get("cuenta/" + cuenta.trim(), CuentaModel.class);

            if (cuentaModel != null) {
                request.setAttribute("cuenta", cuentaModel);
            } else {
                request.setAttribute("error", "No se encontraron datos para la cuenta especificada");
            }
        } catch (Exception ex) {
            request.setAttribute("error", "Error al conectar con el servidor: " + ex.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/cuenta.jsp").forward(request, response);
    }
}




