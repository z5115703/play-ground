package com.playground.backend.util;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    
    private static final SecretKey SECRET_KEY = 
            Keys.hmacShaKeyFor(
                "SkdmlDjacjdRlekfksQlalfDufthl0110121728".getBytes()
            );

    public static String generateToken(String userIdString) {
        return Jwts.builder()
                .setSubject(userIdString)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String extractSubject(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
