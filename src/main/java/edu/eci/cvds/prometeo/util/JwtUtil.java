package edu.eci.cvds.prometeo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "MySuperSecretKeyThatIs256BitsLong!!"; // Debe ser la misma que usa el microservicio de usuarios

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }
}
