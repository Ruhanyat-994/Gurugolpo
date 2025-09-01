package com.loginFeature.login.controller;

import com.loginFeature.login.Dto.CommentCreateDto;
import com.loginFeature.login.Dto.CommentDto;
import com.loginFeature.login.entity.Comment;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.service.CommentService;
import com.loginFeature.login.service.UserService;
import com.loginFeature.login.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequestMapping("/api/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VotingService votingService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                        @Valid @RequestBody CommentCreateDto commentDto,
                                        Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            Comment comment = commentService.addComment(postId, commentDto, user.get().getId());
            CommentDto responseDto = commentService.convertToDto(comment);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsForPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsForPost(postId);
        List<CommentDto> commentDtos = commentService.convertToDtoList(comments);
        return ResponseEntity.ok(commentDtos);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @Valid @RequestBody CommentCreateDto commentDto,
                                           Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            Comment updatedComment = commentService.updateComment(commentId, commentDto, user.get().getId());
            CommentDto responseDto = commentService.convertToDto(updatedComment);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                           Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            commentService.deleteComment(commentId, user.get().getId());
            return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{commentId}/votes")
    public ResponseEntity<?> getCommentVoteCount(@PathVariable Long commentId) {
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
