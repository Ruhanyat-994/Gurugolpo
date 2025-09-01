package com.loginFeature.login.controller;

import com.loginFeature.login.Dto.PostCreateDto;
import com.loginFeature.login.Dto.PostDto;
import com.loginFeature.login.entity.Post;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.service.PostService;
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
@RequestMapping("/api/posts")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VotingService votingService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostCreateDto postDto, Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            Post createdPost = postService.createPost(postDto, user.get().getId());
            PostDto responseDto = postService.convertToDto(createdPost);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        List<PostDto> postDtos = postService.convertToDtoList(posts);
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent()) {
            PostDto postDto = postService.convertToDto(post.get());
            return ResponseEntity.ok(postDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/university/{university}")
    public ResponseEntity<List<PostDto>> getPostsByUniversity(@PathVariable String university) {
        List<Post> posts = postService.getPostsByUniversity(university);
        List<PostDto> postDtos = postService.convertToDtoList(posts);
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto>> searchPosts(@RequestParam("q") String query) {
        query = query.trim();
        List<Post> posts = postService.searchPosts(query);
        List<PostDto> postDtos = postService.convertToDtoList(posts);
        return ResponseEntity.ok(postDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @Valid @RequestBody PostCreateDto postDto, Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            Optional<Post> existingPost = postService.getPostById(id);
            if (existingPost.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Post post = existingPost.get();
            if (!post.getAuthorId().equals(user.get().getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "You can only edit your own posts"));
            }
            
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent());
            Post updatedPost = postService.updatePost(post);
            PostDto responseDto = postService.convertToDto(updatedPost);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication) {
        try {
            Optional<User> user = userService.getUserByEmail(authentication.getName());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            Optional<Post> existingPost = postService.getPostById(id);
            if (existingPost.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Post post = existingPost.get();
            if (!post.getAuthorId().equals(user.get().getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "You can only delete your own posts"));
            }
            
            postService.deletePost(id);
            return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/votes")
    public ResponseEntity<?> getPostVoteCount(@PathVariable Long id) {
        try {
            Map<String, Long> voteCount = votingService.getPostVoteCount(id);
            return ResponseEntity.ok(voteCount);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
