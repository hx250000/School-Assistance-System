package org.example.back.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static String SECRET;
    private static long EXPIRATION_TIME = 3600_000 * 24 ; // 24小时

    @Value("${campus.jwt.secret}")
    public void setSecret(String secret) {
        SECRET = secret;
        System.out.println("Secret: " + SECRET);
    }

    @Value("${campus.jwt.expiration}")
    public void setExpiration(long expiration) {
        EXPIRATION_TIME = expiration;
        System.out.println("EXPIRATION: " + EXPIRATION_TIME);
    }

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody();
        return Long.valueOf(claims.getSubject());
    }
}