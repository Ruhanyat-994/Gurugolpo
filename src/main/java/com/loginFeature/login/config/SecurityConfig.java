package com.loginFeature.login.config;

import com.loginFeature.login.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("=== Configuring Security Filter Chain ===");
        
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public web pages
                        .requestMatchers("/", "/home", "/login", "/post/**", "/want-to-know", "/summary", "/advertise", "/forgot-password", "/register", "/terms", "/privacy", "/error", "/test").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/favicon.ico").permitAll()
                        
                        // Public API endpoints
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/debug").permitAll()
                        .requestMatchers("/api/auth/test-token").permitAll()
                        .requestMatchers("/api/auth/debug-step-by-step").permitAll()
                        .requestMatchers("/api/auth/test-auth").permitAll()
                        .requestMatchers("/api/auth/test-password").permitAll()
                        .requestMatchers("/api/auth/debug-bcrypt").permitAll()
                        .requestMatchers("/api/auth/generate-bcrypt").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts").permitAll() // Allow viewing posts without auth
                        .requestMatchers(HttpMethod.GET, "/api/posts/*").permitAll() // Allow viewing individual posts without auth
                        .requestMatchers(HttpMethod.GET, "/api/posts/*/votes").permitAll() // Allow viewing post vote counts without auth
                        .requestMatchers("/api/posts/search/**").permitAll()
                        .requestMatchers("/api/posts/university/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comments/*/votes").permitAll() // Allow viewing comment vote counts without auth
                        .requestMatchers("/api/universities").permitAll() // Allow viewing universities list
                        
                        // Swagger/OpenAPI documentation
                        .requestMatchers("/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**",
                                "/swagger-resources", "/swagger-resources/**",
                                "/configuration/ui", "/configuration/security",
                                "/swagger-ui/**", "/webjars/**", "/swagger-ui.html",
                                "/docs/**").permitAll()
                        
                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        // Moderator endpoints (inherits USER permissions + extra)
                        .requestMatchers("/api/moderator/**").hasAnyRole("MODERATOR", "ADMIN")
                        
                        // User endpoints (authenticated users with USER role or higher)
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        .requestMatchers("/api/posts/create").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        .requestMatchers("/api/posts/*/edit").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        .requestMatchers("/api/posts/*/delete").hasAnyRole("MODERATOR", "ADMIN") // Only moderators can delete
                        .requestMatchers(HttpMethod.POST, "/api/comments/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/comments/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll() // Allow viewing comments without auth
                        .requestMatchers("/api/votes/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        
                        .anyRequest().authenticated()
                )
                .sessionManagement(config -> config
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        System.out.println("Security configuration: /api/posts/create requires authentication");
        System.out.println("JWT Filter added before UsernamePasswordAuthenticationFilter");
        System.out.println("=== Security Filter Chain Configured ===");
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            
            @Override
            public String encode(CharSequence rawPassword) {
                return bcrypt.encode(rawPassword);
            }
            
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                // If the stored password looks like BCrypt, use BCrypt
                if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
                    return bcrypt.matches(rawPassword, encodedPassword);
                }
                // Otherwise, do plain text comparison (for testing)
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
