package com.loginFeature.login.repository;

import com.loginFeature.login.LoginApplication;
import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BlogRepository extends JpaRepository<Blog, UUID> {
@EntityGraph(attributePaths = {"comments","votes"})
List<Blog> findAll();

}
