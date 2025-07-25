package com.loginFeature.login.service;

import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.UserRepsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepsitory userRepsitory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean registerUser(String username, String password, String role, String university) {
        if (userRepsitory.findByUsername(username) != null) {
            return false; // User already exists
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role);
        newUser.setUniversity(university);
        userRepsitory.save(newUser);
        return true;
    }

    public User getUser(String username){
        return userRepsitory.findByUsername(username);
    }
}
