package com.loginFeature.login.repository;

import com.loginFeature.login.LoginApplication;
import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.entity.User;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BlogRepository extends JpaRepository<Blog, UUID> {
@EntityGraph(attributePaths = {"comments","votes"})
List<Blog> findAll();

    @Query("""
    SELECT b FROM Blog b 
    WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
       OR b.content LIKE CONCAT('%', :keyword, '%')
""")
    List<Blog> searchByKeyWord(@Param("keyword") String keyword);


List<Blog> findAllByOrderByCreatedAtDesc();

List<Blog> findAllByOrderByVoteCountDesc();


}
