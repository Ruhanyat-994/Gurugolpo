package com.loginFeature.login.controller;

import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.UserRepsitory;
import com.loginFeature.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/public/register")
    public ResponseEntity<String> register(@RequestBody User user){
        boolean success = userService.registerUser(
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                user.getUniversity()
        );

        if (success) {
            return ResponseEntity.ok("User Registry Successful!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
    }


    @GetMapping("/public")
    public String profile(){
        return "You are Authenticated";
    }

}
