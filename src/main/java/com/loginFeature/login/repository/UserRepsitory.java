package com.loginFeature.login.repository;

import com.loginFeature.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepsitory extends JpaRepository<User,String> {
    User findByUsername(String username);
}
