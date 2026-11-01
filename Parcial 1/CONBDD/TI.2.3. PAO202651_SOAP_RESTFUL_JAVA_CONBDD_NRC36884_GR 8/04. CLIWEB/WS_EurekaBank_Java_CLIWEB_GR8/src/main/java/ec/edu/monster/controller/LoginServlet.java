package ec.edu.monster.controller;

import ec.edu.monster.model.LoginRequest;
import ec.edu.monster.model.LoginResponse;
import ec.edu.monster.servicio.RestApiClient;
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

        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Por favor complete todos los campos");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        try {
            RestApiClient restClient = new RestApiClient();
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(username.trim());
            loginRequest.setPassword(password.trim());

            // El servidor Java retorna un boolean directamente
            Boolean authResult = restClient.post("login", loginRequest, Boolean.class);
            
            if (authResult != null && authResult) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username.trim());
                response.sendRedirect(request.getContextPath() + "/Home");
            } else {
                request.setAttribute("error", "Credenciales incorrectas");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            request.setAttribute("error", "Error al conectar con el servidor: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}


