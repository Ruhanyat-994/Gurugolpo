package com.loginFeature.login.utility;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class InputSanitizer {
    
    // Patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._-]{3,50}$"
    );
    
    private static final Pattern UNIVERSITY_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9\\s.-]{2,100}$"
    );
    
    // XSS prevention patterns
    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
        "<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile(
        "javascript:", Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern ONLOAD_PATTERN = Pattern.compile(
        "onload\\s*=", Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern ONCLICK_PATTERN = Pattern.compile(
        "onclick\\s*=", Pattern.CASE_INSENSITIVE
    );

    /**
     * Sanitize input to prevent XSS attacks
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove script tags
        input = SCRIPT_PATTERN.matcher(input).replaceAll("");
        
        // Remove javascript: protocols
        input = JAVASCRIPT_PATTERN.matcher(input).replaceAll("");
        
        // Remove event handlers
        input = ONLOAD_PATTERN.matcher(input).replaceAll("");
        input = ONCLICK_PATTERN.matcher(input).replaceAll("");
        
        // HTML encode special characters
        input = input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;")
                    .replace("/", "&#x2F;");
        
        return input.trim();
    }

    /**
     * Validate email format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate username format
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Validate university name format
     */
    public boolean isValidUniversity(String university) {
        if (university == null || university.trim().isEmpty()) {
            return false;
        }
        return UNIVERSITY_PATTERN.matcher(university.trim()).matches();
    }

    /**
     * Validate password strength
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // Check for at least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        
        return hasLetter && hasNumber;
    }

    /**
     * Sanitize and validate text content (for posts and comments)
     */
    public String sanitizeTextContent(String content) {
        if (content == null) {
            return null;
        }
        
        // Basic sanitization
        content = sanitizeInput(content);
        
        // Remove excessive whitespace
        content = content.replaceAll("\\s+", " ");
        
        // Limit length (basic protection against DoS)
        if (content.length() > 10000) {
            content = content.substring(0, 10000);
        }
        
        return content.trim();
    }

    /**
     * Validate and sanitize post title
     */
    public String sanitizePostTitle(String title) {
        if (title == null) {
            return null;
        }
        
        title = sanitizeInput(title);
        
        // Limit length
        if (title.length() > 500) {
            title = title.substring(0, 500);
        }
        
        return title.trim();
    }

    /**
     * Validate and sanitize comment content
     */
    public String sanitizeCommentContent(String content) {
        if (content == null) {
            return null;
        }
        
        content = sanitizeInput(content);
        
        // Limit length
        if (content.length() > 2000) {
            content = content.substring(0, 2000);
        }
        
        return content.trim();
    }
}
