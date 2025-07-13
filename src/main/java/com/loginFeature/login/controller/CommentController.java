package com.loginFeature.login.controller;

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

        Comment comment = commentService.addComment(blogId,content, name);
        return ResponseEntity.ok(comment);
    }

}
