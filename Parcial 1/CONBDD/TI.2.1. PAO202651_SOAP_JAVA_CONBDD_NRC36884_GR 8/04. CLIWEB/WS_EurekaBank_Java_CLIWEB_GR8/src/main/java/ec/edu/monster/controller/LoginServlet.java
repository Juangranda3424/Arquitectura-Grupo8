package ec.edu.monster.controller;

import ec.edu.monster.servicio.EurekaBankSoapClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/Login", "/Login/Index"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Si ya está autenticado, redirigir al menú
        if (session != null && session.getAttribute("username") != null) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Por favor complete todos los campos");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        try {
            EurekaBankSoapClient soapClient = new EurekaBankSoapClient();

            // El servidor compara el usuario y la clave tal como llegan
            if (soapClient.autenticar(username.trim(), password.trim())) {
                HttpSession session = request.getSession(true);
                session.setAttribute("username", username.trim());
                response.sendRedirect(request.getContextPath() + "/Home");
            } else {
                request.setAttribute("error", "Credenciales incorrectas");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
        } catch (RuntimeException ex) {
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
