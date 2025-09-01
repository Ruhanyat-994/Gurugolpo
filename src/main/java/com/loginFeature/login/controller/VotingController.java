package com.loginFeature.login.controller;

import com.loginFeature.login.entity.PostVote;
import com.loginFeature.login.entity.CommentVote;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.enums.VoteType;
import com.loginFeature.login.service.UserService;
import com.loginFeature.login.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequestMapping("/api/votes")
public class VotingController {

    @Autowired
    private VotingService votingService;

    @Autowired
    private UserService userService;

    @PostMapping("/posts/{postId}/upvote")
    public ResponseEntity<?> upvotePost(@PathVariable Long postId, Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String message = votingService.voteOnPost(postId, user.get().getId(), VoteType.UPVOTE);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/posts/{postId}/downvote")
    public ResponseEntity<?> downvotePost(@PathVariable Long postId, Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String message = votingService.voteOnPost(postId, user.get().getId(), VoteType.DOWNVOTE);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/comments/{commentId}/upvote")
    public ResponseEntity<?> upvoteComment(@PathVariable Long commentId, Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String message = votingService.voteOnComment(commentId, user.get().getId(), VoteType.UPVOTE);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/comments/{commentId}/downvote")
    public ResponseEntity<?> downvoteComment(@PathVariable Long commentId, Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String message = votingService.voteOnComment(commentId, user.get().getId(), VoteType.DOWNVOTE);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/posts/{postId}/count")
    public ResponseEntity<?> getPostVoteCounts(@PathVariable Long postId) {
        try {
            Map<String, Long> voteCount = votingService.getPostVoteCount(postId);
            return ResponseEntity.ok(voteCount);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/comments/{commentId}/count")
    public ResponseEntity<?> getCommentVoteCounts(@PathVariable Long commentId) {
        try {
            Map<String, Long> voteCount = votingService.getCommentVoteCount(commentId);
            return ResponseEntity.ok(voteCount);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}