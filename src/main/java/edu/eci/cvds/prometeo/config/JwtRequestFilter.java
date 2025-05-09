package edu.eci.cvds.prometeo.config;

import edu.eci.cvds.prometeo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {

                var claims = jwtUtil.extractClaims(authHeader);

                String username = claims.get("username", String.class); // si quieres usarlo
                String role = claims.get("role", String.class);
                String name = claims.get("name", String.class);
                String idCard = claims.get("idCard", String.class); // ← este es el institutionalId

                // Guardar en request para que los controladores lo usen
                request.setAttribute("username", username); // opcional
                request.setAttribute("role", role);
                request.setAttribute("name", name);
                request.setAttribute("institutionalId", idCard);

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
