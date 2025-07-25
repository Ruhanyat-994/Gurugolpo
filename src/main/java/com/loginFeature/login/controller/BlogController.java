package com.loginFeature.login.controller;

import com.loginFeature.login.Dto.BlogDto;
import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.BlogRepository;
import com.loginFeature.login.service.BlogService;
import com.loginFeature.login.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequestMapping("/api/blogs")
public class BlogController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private VotingService votingService;

    @PostMapping("/create")
    public ResponseEntity<?> createBlog(@RequestBody Blog blog,Authentication authentication){
        blog.setAuthor(authentication.getName());
        Blog createdBlog = blogService.createBlog(blog,authentication.getName());
        return ResponseEntity.ok(createdBlog);


    }

    // this will get all blogs by the newest blog
    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs(){
        return ResponseEntity.ok(blogService.getAllBlogsSortedByNewest());
    }


    // it will update the blog by id
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

    // it will delete the blog by Id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable UUID id, Authentication authentication) {

        blogService.deleteBlog(id);
        return ResponseEntity.ok("The blog has been deleted.");
    }

//    @GetMapping("/{id}/votes")
//    public ResponseEntity<Map<String, Long>> getVoteCount(@PathVariable UUID id){
//        Blog blog = blogRepository.findById(id).orElseThrow(()-> new RuntimeException("Blog not Found"));
//        long upvotes = blog.getUpVote();
//        long downvotes = blog.getDownVote();
//
//        Map<String, Long> response = new HashMap<>();
//        response.put("upvotes", upvotes);
//        response.put("downvotes", downvotes);
//
//        return ResponseEntity.ok(response);
//    }
@GetMapping("/popular")
public ResponseEntity<List<Blog>> getPopularBlogs() {
    return ResponseEntity.ok(votingService.getAllBlogsSortedByPopularity());
}
// get the blog by search
@GetMapping("/search")
public ResponseEntity<List<Blog>> searchBlog(@RequestParam("q") String query){
        query = query.trim();
        return ResponseEntity.ok(blogService.searchBlog(query));
}



}
