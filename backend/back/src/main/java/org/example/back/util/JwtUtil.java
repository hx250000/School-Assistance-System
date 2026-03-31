package org.example.back.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .signWith(SignatureAlgorithm.HS256, "yourverylongsecretkeyshouldbeatleast32bytes")
                .compact();
    }
}