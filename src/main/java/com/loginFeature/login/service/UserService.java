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

    public void registerUser(String username, String passowrd, String role, String university){
        String hashed = passwordEncoder.encode(passowrd);
        userRepsitory.save(new User(username,hashed,role,university));

    }
    public User getUser(String username){
        return userRepsitory.findByUsername(username);
    }
}
