package ec.edu.monster.banquito.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.monster.banquito.dto.JwtAuthError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException
    ) throws ServletException, IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, JwtAuthError.builder()
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .exceptionClass(authException.getClass().getName())
                .message(authException.getMessage()).build());
        responseStream.flush();
    }
}
