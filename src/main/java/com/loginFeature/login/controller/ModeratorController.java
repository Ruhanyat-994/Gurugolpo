package com.loginFeature.login.controller;

import com.loginFeature.login.Dto.ModeratorDashboardDto;
import com.loginFeature.login.Dto.PostDto;
import com.loginFeature.login.entity.Post;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.service.ModeratorService;
import com.loginFeature.login.service.PostService;
import com.loginFeature.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/moderator")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ModeratorController {

    @Autowired
    private ModeratorService moderatorService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String university = user.get().getUniversity();
            ModeratorDashboardDto dashboard = moderatorService.getDashboardData(university);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/pending-posts")
    public ResponseEntity<?> getPendingPosts(Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String university = user.get().getUniversity();
            List<Post> pendingPosts = postService.getPendingPostsByUniversity(university);
            List<PostDto> postDtos = postService.convertToDtoList(pendingPosts);
            return ResponseEntity.ok(postDtos);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/posts/{postId}/approve")
    public ResponseEntity<?> approvePost(@PathVariable Long postId, Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String university = user.get().getUniversity();
            Post approvedPost = moderatorService.approvePost(postId, university);
            PostDto postDto = postService.convertToDto(approvedPost);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Post approved successfully");
            response.put("post", postDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/posts/{postId}/reject")
    public ResponseEntity<?> rejectPost(@PathVariable Long postId, Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String university = user.get().getUniversity();
            Post rejectedPost = moderatorService.rejectPost(postId, university);
            PostDto postDto = postService.convertToDto(rejectedPost);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Post rejected successfully");
            response.put("post", postDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String university = user.get().getUniversity();
            moderatorService.deletePost(postId, university);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
