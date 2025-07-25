package com.loginFeature.login.controller;

import com.loginFeature.login.Dto.CommentDto;
import com.loginFeature.login.entity.Comment;
import com.loginFeature.login.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.CustomSQLErrorCodesTranslation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/{blogId}")
    public ResponseEntity<?> addComment(@PathVariable UUID blogId,
                                        @RequestBody Map<String, String> body,
                                        Authentication authentication){
        String content = body.get("content");
        String name = authentication.getName();

        CommentDto comment = commentService.addComment(blogId,content, name);
        return ResponseEntity.ok(comment);
    }
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable UUID commentId,
                                        @RequestBody Map<String, String> body,
                                        Authentication authentication){
        String content = body.get("content");
        String name = authentication.getName();

        Comment updateComment = commentService.updateComment(commentId,content,name);
        return ResponseEntity.ok(updateComment);

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID commentId,
                                           Authentication authentication){
        String username = authentication.getName();
        commentService.deleteComment(commentId,username);
        return ResponseEntity.noContent().build();
    }

}
