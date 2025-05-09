package edu.eci.cvds.prometeo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "mi_clave_secreta"; // Debe ser la misma que usa el microservicio de usuarios

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject(); // normalmente es el "sub"
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
}
