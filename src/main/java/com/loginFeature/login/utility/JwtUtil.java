package com.loginFeature.login.utility;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
        .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*10))
                .claim("jti", UUID.randomUUID().toString())
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes())
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    public boolean isTokenExpired(String token){
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes())
                .build().parseClaimsJws(token).getBody().
                getExpiration().before(new Date());
    }




}
