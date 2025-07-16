package com.loginFeature.login.controller;

import com.loginFeature.login.Dto.BlogDto;
import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.repository.BlogRepository;
import com.loginFeature.login.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @PostMapping("/create")
    public ResponseEntity<?> createBlog(@RequestBody Blog blog, Authentication authentication){
        blog.setAuthor(authentication.getName());
        Blog createdBlog = blogService.createBlog(blog);
        return ResponseEntity.ok(createdBlog);


    }

    @GetMapping
    public ResponseEntity<?> getAllBlogs(){
        try {
            List<BlogDto> blogs = blogService.getAllBlogs();
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable UUID id, @RequestBody Blog updatedBlog, Authentication authentication){
        Blog blogById = blogService.getBlogById(id);
        if(blogById == null){
            return ResponseEntity.notFound().build();
        }
        if(!blogById.getAuthor().equals(authentication.getName())){
            return ResponseEntity.status(403).body("You are not authenticated to update this blog.");
        }
        updatedBlog.setId(id);
        updatedBlog.setAuthor(blogById.getAuthor());

        Blog blog = blogService.updateBlog(updatedBlog);

        return ResponseEntity.ok(blog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable UUID id, Authentication authentication) {

        blogService.deleteBlog(id);
        return ResponseEntity.ok("The blog has been deleted.");
    }
}
