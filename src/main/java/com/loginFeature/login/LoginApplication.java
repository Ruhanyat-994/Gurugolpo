package com.loginFeature.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import com.loginFeature.login.utility.JwtUtil;
import com.loginFeature.login.service.UniversityService;

@SpringBootApplication
public class LoginApplication implements CommandLineRunner {

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UniversityService universityService;

	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Validate JWT configuration on startup
		System.out.println("=== JWT Configuration Validation ===");
		jwtUtil.validateJwtConfiguration();
		System.out.println("===================================");
		
		// Initialize default universities
		System.out.println("=== Initializing Default Universities ===");
		try {
			universityService.initializeDefaultUniversities();
			System.out.println("Universities initialized successfully");
		} catch (Exception e) {
			System.err.println("Failed to initialize universities: " + e.getMessage());
			System.err.println("Please create the universities table first");
		}
		System.out.println("===================================");
	}
}
