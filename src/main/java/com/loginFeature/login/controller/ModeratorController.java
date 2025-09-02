package com.loginFeature.login.controller;

import com.loginFeature.login.entity.Post;
import com.loginFeature.login.entity.Comment;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.service.PostService;
import com.loginFeature.login.service.CommentService;
import com.loginFeature.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/moderator")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
public class ModeratorController {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private UserService userService;
    
    // Delete a post (only moderators and admins)
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        try {
            postService.deletePost(postId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete post: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Delete a comment (only moderators and admins)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteCommentAsModerator(commentId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Comment deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete comment: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Ban a user (only moderators and admins)
    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<Map<String, String>> banUser(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setIsActive(false);
            userService.updateUser(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User banned successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to ban user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Unban a user (only moderators and admins)
    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<Map<String, String>> unbanUser(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setIsActive(true);
            userService.updateUser(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User unbanned successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to unban user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Promote user to moderator (only admins)
    @PostMapping("/users/{userId}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> promoteToModerator(@PathVariable Long userId, @RequestParam String university) {
        try {
            User promotedUser = userService.promoteToModerator(userId, university);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User promoted to moderator successfully");
            response.put("username", promotedUser.getUsername());
            response.put("university", promotedUser.getUniversity());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to promote user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Get moderator dashboard data
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getModeratorDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Get counts for moderation
            long totalPosts = postService.getPostCount();
            long totalComments = commentService.getCommentCount();
            long totalUsers = userService.getUserCount();
            
            dashboard.put("totalPosts", totalPosts);
            dashboard.put("totalComments", totalComments);
            dashboard.put("totalUsers", totalUsers);
            dashboard.put("message", "Moderator dashboard data retrieved successfully");
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to get dashboard data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
