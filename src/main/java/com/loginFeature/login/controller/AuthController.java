package com.loginFeature.login.controller;

import com.loginFeature.login.Dto.AuthRequest;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.service.UserService;
import com.loginFeature.login.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(),
                    authRequest.getPassword()));

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
            final String jwtToken = jwtUtil.generateToken(userDetails);
            
            // Get user details for response
            Optional<User> user = userService.getUserByEmail(authRequest.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtToken);
            response.put("user", user.orElse(null));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/debug")
    public ResponseEntity<?> debugAuth(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtil.extractUsername(token);
                boolean isValid = jwtUtil.validateToken(token, userDetailsService.loadUserByUsername(email));
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("email", email);
                response.put("isValid", isValid);
                response.put("message", "Token debug information");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing or invalid Authorization header"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Debug failed: " + e.getMessage()));
        }
    }

    @GetMapping("/test-token")
    public ResponseEntity<?> testToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Test the token directly
                jwtUtil.testSpecificToken(token);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Token test completed - check console logs");
                response.put("token", token);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing or invalid Authorization header"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token test failed: " + e.getMessage()));
        }
    }

    @GetMapping("/debug-step-by-step")
    public ResponseEntity<?> debugStepByStep(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Debug the token step by step
                jwtUtil.debugTokenStepByStep(token);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Step-by-step debug completed - check console logs");
                response.put("token", token);
                response.put("tokenLength", token.length());
                response.put("tokenParts", token.split("\\.").length);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing or invalid Authorization header"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Debug failed: " + e.getMessage()));
        }
    }

    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuthentication(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null) {
            response.put("authenticated", true);
            response.put("principal", authentication.getPrincipal());
            response.put("authorities", authentication.getAuthorities());
            response.put("name", authentication.getName());
            response.put("message", "Authentication successful!");
        } else {
            response.put("authenticated", false);
            response.put("message", "No authentication found!");
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test-password")
    public ResponseEntity<?> testPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            
            // Get user from database
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            User user = userOpt.get();
            String storedPassword = user.getPassword();
            
            // Test password matching
            boolean matches = passwordEncoder.matches(password, storedPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("storedPassword", storedPassword);
            response.put("providedPassword", password);
            response.put("matches", matches);
            response.put("userActive", user.getIsActive());
            response.put("userRole", user.getRole());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/debug-bcrypt")
    public ResponseEntity<?> debugBcrypt() {
        try {
            // Test with known working hash
            String testPassword = "admin";
            String knownHash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HFz8rJ7Jmt3yV1zXfVhK2";
            
            boolean matches = passwordEncoder.matches(testPassword, knownHash);
            
            Map<String, Object> response = new HashMap<>();
            response.put("testPassword", testPassword);
            response.put("knownHash", knownHash);
            response.put("matches", matches);
            response.put("passwordEncoderClass", passwordEncoder.getClass().getName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/generate-bcrypt")
    public ResponseEntity<?> generateBcrypt(@RequestParam String password) {
        try {
            // Generate a new BCrypt hash for the given password
            String hash = passwordEncoder.encode(password);
            
            // Test if the generated hash matches the password
            boolean matches = passwordEncoder.matches(password, hash);
            
            Map<String, Object> response = new HashMap<>();
            response.put("password", password);
            response.put("generatedHash", hash);
            response.put("matches", matches);
            response.put("passwordEncoderClass", passwordEncoder.getClass().getName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Not authenticated"));
            }
            
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
