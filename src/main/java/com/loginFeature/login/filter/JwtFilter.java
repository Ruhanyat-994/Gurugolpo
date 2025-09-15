package com.loginFeature.login.filter;

import com.loginFeature.login.utility.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    // List of public endpoints to skip JWT validation
    private static final List<String> PUBLIC_URLS = List.of(
            // Web pages
            "/",
            "/home",
            "/login",
            "/post/",
            "/want-to-know",
            "/summary",
            "/advertise",
            "/forgot-password",
            "/register",
            "/terms",
            "/privacy",
            // Static resources
            "/favicon.ico",
            "/css/",
            "/js/",
            "/images/",
            "/static/",
            // API endpoints
            "/api/public/register",
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/debug",
            "/api/auth/test-token",
            "/api/auth/debug-step-by-step",
            "/api/auth/test-auth",
            "/api/universities",
            // Swagger/OpenAPI
            "/v2/api-docs",
            "/v3/api-docs",
            "/swagger-ui",
            "/docs"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        
        System.out.println("=== JWT Filter Processing ===");
        System.out.println("Path: " + path);
        System.out.println("Method: " + method);

        // Only apply JWT filter to API endpoints
        if (!path.startsWith("/api/")) {
            System.out.println("Not an API endpoint, skipping JWT filter");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("Is Public API Endpoint: " + isPublicEndpoint(path, method));

        // Skip JWT validation for public API endpoints
        if (isPublicEndpoint(path, method)) {
            System.out.println("Skipping JWT validation for public API endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("Processing protected endpoint - JWT validation required");

        // JWT validation for protected requests
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + (authHeader != null ? authHeader.substring(0, Math.min(50, authHeader.length())) + "..." : "NULL"));
        
        String email = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("Extracted JWT Token: " + jwt.substring(0, Math.min(50, jwt.length())) + "...");
            
            try {
                email = jwtUtil.extractUsername(jwt);
                System.out.println("Extracted Email: " + email);
            } catch (Exception e) {
                System.err.println("ERROR: Failed to extract username from JWT: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT token - extraction failed");
                return;
            }
        } else {
            System.err.println("ERROR: Missing or invalid Authorization header for path: " + path);
            System.err.println("Header value: " + authHeader);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Loading user details for email: " + email);
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                System.out.println("User details loaded successfully");
                System.out.println("Username: " + userDetails.getUsername());
                System.out.println("Authorities: " + userDetails.getAuthorities());
                System.out.println("Account non-expired: " + userDetails.isAccountNonExpired());
                System.out.println("Account non-locked: " + userDetails.isAccountNonLocked());
                System.out.println("Credentials non-expired: " + userDetails.isCredentialsNonExpired());
                System.out.println("Enabled: " + userDetails.isEnabled());

                System.out.println("Validating JWT token...");
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    System.out.println("JWT validation SUCCESSFUL");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication context set successfully");
                    System.out.println("Security Context Authentication: " + SecurityContextHolder.getContext().getAuthentication());
                } else {
                    System.err.println("ERROR: JWT validation FAILED for user: " + email);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid JWT token - validation failed");
                    return;
                }
            } catch (Exception e) {
                System.err.println("ERROR: JWT validation failed for user: " + email + " - " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Authentication failed: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("Authentication context already exists or email is null");
            System.out.println("Email: " + email);
            System.out.println("Existing Authentication: " + SecurityContextHolder.getContext().getAuthentication());
        }

        System.out.println("Proceeding to endpoint with authentication: " + SecurityContextHolder.getContext().getAuthentication());
        
        // CRITICAL: Check if authentication is properly set
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            System.out.println("✅ Authentication context is SET - proceeding to endpoint");
            System.out.println("Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            System.out.println("Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            System.out.println("Is Authenticated: " + SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
        } else {
            System.err.println("❌ CRITICAL ERROR: Authentication context is NULL - this will cause 403!");
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path, String method) {
        System.out.println("=== Checking if endpoint is public ===");
        System.out.println("Path: " + path);
        System.out.println("Method: " + method);
        
        // Allow registration endpoint without authentication
        if (path.equals("/api/auth/register") && "POST".equalsIgnoreCase(method)) {
            System.out.println("✅ Public registration endpoint detected");
            return true;
        }
        
        // Allow only specific read-only GET requests to posts and comments without authentication
        if ("GET".equalsIgnoreCase(method)) {
            // Allow viewing posts, searching posts, and university-specific posts
            if (path.equals("/api/posts") || 
                path.startsWith("/api/posts/search") || 
                path.startsWith("/api/posts/university/") ||
                path.matches("/api/posts/\\d+") || // Allow viewing specific post by ID
                path.matches("/api/comments/\\d+")) { // Allow viewing comments for specific post
                System.out.println("✅ Public GET endpoint detected");
                return true;
            }
        }
        
        // Check for web pages and static resources
        if (path.equals("/") || 
            path.equals("/home") ||
            path.equals("/login") || 
            path.equals("/error") ||
            path.equals("/favicon.ico") ||
            path.equals("/test") ||
            path.startsWith("/post/") ||
            path.equals("/want-to-know") ||
            path.equals("/summary") ||
            path.equals("/advertise") ||
            path.equals("/forgot-password") ||
            path.equals("/register") ||
            path.equals("/terms") ||
            path.equals("/privacy") ||
            path.startsWith("/css/") ||
            path.startsWith("/js/") ||
            path.startsWith("/images/") ||
            path.startsWith("/static/")) {
            System.out.println("✅ Public web page or static resource detected");
            return true;
        }
        
        // Check exact public URLs and Swagger UI paths
        boolean isPublic = PUBLIC_URLS.stream()
                .anyMatch(publicUrl -> {
                    boolean exactMatch = path.equals(publicUrl);
                    boolean swaggerMatch = publicUrl.equals("/swagger-ui") && path.startsWith("/swagger-ui");
                    boolean docsMatch = publicUrl.equals("/docs") && path.startsWith("/docs");
                    boolean apiDocsMatch = (publicUrl.equals("/v2/api-docs") || publicUrl.equals("/v3/api-docs")) && 
                                         (path.startsWith("/v2/api-docs") || path.startsWith("/v3/api-docs"));
                    
                    boolean isMatch = exactMatch || swaggerMatch || docsMatch || apiDocsMatch;
                    System.out.println("Checking: " + publicUrl + " -> exact: " + exactMatch + 
                                     ", swagger: " + swaggerMatch + ", docs: " + docsMatch + 
                                     ", apiDocs: " + apiDocsMatch);
                    return isMatch;
                });
        
        System.out.println("Final result - Is public endpoint: " + isPublic);
        return isPublic;
    }
}
