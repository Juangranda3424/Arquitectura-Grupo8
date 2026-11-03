package ec.edu.monster.phoneshop.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().equals("/error")) {
            return true;
        }

        if (request.getRequestURI().startsWith("/assets")) {
            return true;
        }

        if (request.getSession().getAttribute("AUTH_TOKEN") == null && !request.getRequestURI().equals("/login")) {
            response.sendRedirect("/login");
            return false;
        }

        if (request.getRequestURI().equals("/login") && request.getSession().getAttribute("AUTH_TOKEN") != null) {
            response.sendRedirect("/");
            return false;
        }

        if (request.getSession().getAttribute("AUTH_TOKEN") != null && request.getSession().getAttribute("AUTHENTICATED_USER") == null) {
            request.getSession().removeAttribute("AUTH_TOKEN");
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        if (modelAndView != null && request.getSession().getAttribute("AUTHENTICATED_USER") != null) {
            modelAndView.addObject("authenticatedUser", request.getSession().getAttribute("AUTHENTICATED_USER"));
        }
    }
}
