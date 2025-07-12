package com.loginFeature.login.repository;

import com.loginFeature.login.LoginApplication;
import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {

}
