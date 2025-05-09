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

        System.out.println("🔍 Checking Authorization header...");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            System.out.println("✅ Authorization header found: " + authHeader);

            try {
                var claims = jwtUtil.extractClaims(authHeader);

                String username = claims.get("username", String.class);
                String role = claims.get("role", String.class);
                String name = claims.get("name", String.class);
                String idCard = claims.get("idCard", String.class);

                // Log extracted claims
                System.out.println("✅ JWT Claims extracted:");
                System.out.println("username = " + username);
                System.out.println("role = " + role);
                System.out.println("name = " + name);
                System.out.println("idCard = " + idCard);

                // Save attributes in the request
                request.setAttribute("username", username);
                request.setAttribute("role", role);
                request.setAttribute("name", name);
                request.setAttribute("institutionalId", idCard);

            } catch (Exception e) {
                System.out.println("❌ Error extracting JWT claims: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        } else {
            System.out.println("⚠️ Authorization header is missing or does not start with 'Bearer '");
        }

        chain.doFilter(request, response);
    }
}
