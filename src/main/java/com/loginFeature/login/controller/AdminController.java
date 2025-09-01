package com.loginFeature.login.controller;

import com.loginFeature.login.Dto.AdminDashboardDto;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.service.AdminService;
import com.loginFeature.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboard() {
        AdminDashboardDto dashboard = adminService.getDashboardData();
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/settings/post-management")
    public ResponseEntity<?> togglePostManagement(@RequestBody Map<String, Boolean> request) {
        boolean enabled = request.getOrDefault("enabled", false);
        adminService.togglePostManagement(enabled);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post management setting updated");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/promote")
    public ResponseEntity<?> promoteUserToModerator(@PathVariable Long userId, 
                                                   @RequestBody Map<String, String> request,
                                                   Authentication authentication) {
        try {
            String university = request.get("university");
            if (university == null || university.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "University is required"));
            }
            
            User promotedUser = adminService.promoteUserToModerator(userId, university);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User promoted to moderator successfully");
            response.put("user", promotedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            adminService.deleteUser(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        try {
            adminService.deletePost(postId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            adminService.deleteComment(commentId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Comment deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
