package com.loginFeature.login.controller;

import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.repository.BlogRepository;
import com.loginFeature.login.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Blog> getAllBlogs(){
        return blogService.getAllBlogs();
    }

}
