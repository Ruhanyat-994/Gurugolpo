package com.loginFeature.login.controller;

import com.loginFeature.login.Dto.PostDto;
import com.loginFeature.login.entity.Post;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.service.PostService;
import com.loginFeature.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) String search, 
                      @RequestParam(required = false) String vibe) {
        System.out.println("=== WebController.home() called ===");
        try {
            List<Post> posts;
            
            if (search != null && !search.trim().isEmpty()) {
                System.out.println("Searching posts with keyword: " + search);
                posts = postService.searchPosts(search.trim());
            } else if (vibe != null && !vibe.trim().isEmpty()) {
                System.out.println("Filtering posts by vibe: " + vibe);
                // Filter by sentiment if needed
                posts = postService.getAllPosts();
                // You could add sentiment filtering here
            } else {
                System.out.println("Getting all posts");
                posts = postService.getAllPosts();
            }
            
            System.out.println("Found " + posts.size() + " posts");
            
            // Convert to DTOs with author names
            List<PostDto> postDtos = postService.convertToDtoListWithAuthorNames(posts);
            System.out.println("Converted to " + postDtos.size() + " DTOs");
            
            model.addAttribute("posts", postDtos);
            System.out.println("Added posts to model, returning home template");
            return "home";
        } catch (Exception e) {
            System.err.println("ERROR in WebController.home(): " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("posts", List.of());
            return "home";
        }
    }

    @GetMapping("/test")
    public String test() {
        System.out.println("=== Test endpoint called ===");
        return "Test endpoint working!";
    }

    @GetMapping("/test-plain")
    public ResponseEntity<String> testPlain() {
        System.out.println("=== Test plain endpoint called ===");
        return ResponseEntity.ok("Plain text response working!");
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        try {
            Optional<Post> postOpt = postService.getPostById(id);
            if (postOpt.isPresent()) {
                Post post = postOpt.get();
                model.addAttribute("post", postService.convertToDtoWithAuthorName(post));
                
                // Load comments for this post
                // You might want to add a method to get comments by post ID
                model.addAttribute("comments", List.of()); // Placeholder
                
                return "post-detail";
            } else {
                return "redirect:/";
            }
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("/create-post")
    public String createPost(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login?redirect=" + java.net.URLEncoder.encode("/create-post", java.nio.charset.StandardCharsets.UTF_8);
        }
        return "create-post";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login?redirect=" + java.net.URLEncoder.encode("/admin/dashboard", java.nio.charset.StandardCharsets.UTF_8);
        }
        
        try {
            Optional<User> userOpt = userService.getUserByEmail(authentication.getName());
            if (userOpt.isPresent() && userOpt.get().getRole().name().equals("ADMIN")) {
                // Load dashboard data
                model.addAttribute("dashboard", new Object() {
                    public long getTotalUsers() { return userService.getUserCount(); }
                    public long getTotalPosts() { return postService.getPostCount(); }
                    public long getTotalComments() { return 0; } // Placeholder
                    public long getTotalModerators() { return userService.getModeratorCount(); }
                });
                return "admin-dashboard";
            } else {
                return "redirect:/";
            }
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("/moderator/dashboard")
    public String moderatorDashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login?redirect=" + java.net.URLEncoder.encode("/moderator/dashboard", java.nio.charset.StandardCharsets.UTF_8);
        }
        
        try {
            Optional<User> userOpt = userService.getUserByEmail(authentication.getName());
            if (userOpt.isPresent() && 
                (userOpt.get().getRole().name().equals("MODERATOR") || userOpt.get().getRole().name().equals("ADMIN"))) {
                // Load dashboard data
                model.addAttribute("dashboard", new Object() {
                    public long getTotalUsers() { return userService.getUserCount(); }
                    public long getTotalPosts() { return postService.getPostCount(); }
                    public long getTotalComments() { return 0; } // Placeholder
                });
                return "moderator-dashboard";
            } else {
                return "redirect:/";
            }
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

    @GetMapping("/want-to-know")
    public String wantToKnow() {
        return "redirect:/"; // Placeholder
    }

    @GetMapping("/summary")
    public String summary() {
        return "redirect:/"; // Placeholder
    }

    @GetMapping("/advertise")
    public String advertise() {
        return "redirect:/"; // Placeholder
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "redirect:/login"; // Placeholder
    }

    @GetMapping("/register")
    public String register() {
        return "redirect:/login"; // Placeholder
    }

    @GetMapping("/terms")
    public String terms() {
        return "redirect:/"; // Placeholder
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "redirect:/"; // Placeholder
    }
}
