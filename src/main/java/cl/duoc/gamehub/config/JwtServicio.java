package cl.duoc.gamehub.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtServicio {

    private final SecretKey llave;
    private final long duracionMs;

    public JwtServicio(
            @Value("${app.jwt.secreto}") String secreto,
            @Value("${app.jwt.duracion-ms:86400000}") long duracionMs) {
        this.llave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.duracionMs = duracionMs;
    }

    public String generarToken(String correo, String rol) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + duracionMs);

        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(llave)
                .compact();
    }

    public Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(llave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}