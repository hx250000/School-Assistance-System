package org.example.back.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {

    private static final String SECRET = "yourverylongsecretkeyshouldbeatleast32bytes";

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
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