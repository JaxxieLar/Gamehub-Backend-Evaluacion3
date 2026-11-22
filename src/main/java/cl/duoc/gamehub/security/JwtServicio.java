package cl.duoc.gamehub.security;

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

    private final SecretKey clave;
    private final long duracionMs;

    public JwtServicio(
            @Value("${app.jwt.secreto}") String secreto,
            @Value("${app.jwt.duracion-ms:86400000}") long duracionMs) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.duracionMs = duracionMs;
    }

    public String generar(String email, String rol) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + duracionMs);

        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(clave)
                .compact();
    }

    public Claims validar(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Alias para compatibilidad con clases que llamen a generarToken o parsearClaims
    public String generarToken(String correo, String rol) {
        return generar(correo, rol);
    }

    public Claims parsearClaims(String token) {
        return validar(token);
    }
}