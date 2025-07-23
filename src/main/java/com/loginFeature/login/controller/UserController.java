package com.loginFeature.login.controller;

import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.UserRepsitory;
import com.loginFeature.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/public/register")
    public String register(@RequestBody User user){
        String username = user.getUsername();
        String password = user.getPassword();
        String university = user.getUniversity();
        String role = user.getRole();
        userService.registerUser(username,password,role,university);
        return "User Registry Successfull!";
    }

    @GetMapping("/public")
    public String profile(){
        return "You are Authenticated";
    }

}
