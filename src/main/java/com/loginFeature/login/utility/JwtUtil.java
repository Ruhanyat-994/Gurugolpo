package com.loginFeature.login.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String generateToken(UserDetails userDetails){
        try {
            SecretKey signingKey = getSigningKey();
            System.out.println("Generated signing key length: " + signingKey.getEncoded().length + " bytes");
            
            // Force current time to fix system time issues
            long currentTime = System.currentTimeMillis();
            System.out.println("Current system time: " + new Date(currentTime));
            System.out.println("Current system time (ms): " + currentTime);
            
            // Validate time is reasonable (should be around 1700000000000 for December 2023)
            if (currentTime > 1800000000000L) { // After 2027
                System.err.println("WARNING: System time appears to be in the future!");
                System.err.println("Expected time should be around: " + new Date(1700000000000L));
            }
            
            return Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(currentTime))
                    .setExpiration(new Date(currentTime + 1000*60*60*10))
                    .claim("jti", UUID.randomUUID().toString())
                    .signWith(signingKey, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            System.err.println("Error generating JWT token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate JWT token: " + e.getMessage());
        }
    }

    public String extractUsername(String token){
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract username from token: " + e.getMessage());
        }
    }

    public boolean validateToken(String token, UserDetails userDetails){
        try {
            return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed: " + e.getMessage());
        }
    }
    
    public boolean isTokenExpired(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
        // Use UTF-8 encoding explicitly and ensure minimum key length
        byte[] keyBytes = SECRET_KEY.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        
        // Ensure the key is at least 256 bits (32 bytes) for HS256
        if (keyBytes.length < 32) {
            // Pad the key to 32 bytes if it's too short
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            return Keys.hmacShaKeyFor(paddedKey);
        }
        
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // Method to validate JWT configuration
    public void validateJwtConfiguration() {
        try {
            SecretKey key = getSigningKey();
            System.out.println("JWT Configuration Validation:");
            System.out.println("- Secret key length: " + SECRET_KEY.length() + " characters");
            System.out.println("- Encoded key length: " + key.getEncoded().length + " bytes");
            System.out.println("- Algorithm: " + key.getAlgorithm());
            
            // Check system time
            long currentTime = System.currentTimeMillis();
            System.out.println("- Current system time: " + new Date(currentTime));
            System.out.println("- Current system time (ms): " + currentTime);
            
            // Test token generation
            String testToken = Jwts.builder()
                    .setSubject("test@example.com")
                    .setIssuedAt(new Date(currentTime))
                    .setExpiration(new Date(currentTime + 1000 * 60 * 60))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            
            System.out.println("- Test token generated successfully");
            System.out.println("- Token length: " + testToken.length() + " characters");
            
            // Test token parsing
            try {
                Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(testToken).getBody();
                System.out.println("- Test token parsed successfully");
                System.out.println("- Subject: " + claims.getSubject());
                System.out.println("- Issued at: " + claims.getIssuedAt());
                System.out.println("- Expiration: " + claims.getExpiration());
            } catch (Exception e) {
                System.err.println("- ERROR: Test token parsing failed: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("JWT Configuration Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Method to test a specific JWT token
    public void testSpecificToken(String token) {
        try {
            System.out.println("=== Testing Specific JWT Token ===");
            System.out.println("Token: " + token);
            
            Claims claims = extractAllClaims(token);
            System.out.println("- Token parsed successfully");
            System.out.println("- Subject: " + claims.getSubject());
            System.out.println("- Issued at: " + claims.getIssuedAt());
            System.out.println("- Expiration: " + claims.getExpiration());
            System.out.println("- Current time: " + new Date());
            System.out.println("- Is expired: " + isTokenExpired(token));
            
        } catch (Exception e) {
            System.err.println("ERROR: Token testing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Method to test token parsing step by step
    public void debugTokenStepByStep(String token) {
        System.out.println("=== Debugging Token Step by Step ===");
        System.out.println("1. Input token: " + token);
        System.out.println("2. Token length: " + token.length());
        System.out.println("3. Token parts: " + token.split("\\.").length);
        
        try {
            System.out.println("4. Getting signing key...");
            SecretKey key = getSigningKey();
            System.out.println("5. Signing key algorithm: " + key.getAlgorithm());
            System.out.println("6. Signing key length: " + key.getEncoded().length + " bytes");
            
            System.out.println("7. Attempting to parse token...");
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            System.out.println("8. SUCCESS: Token parsed successfully!");
            System.out.println("9. Claims: " + claims);
            
        } catch (Exception e) {
            System.err.println("8. FAILED: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    

}
