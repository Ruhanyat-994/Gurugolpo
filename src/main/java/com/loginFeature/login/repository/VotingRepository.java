package com.loginFeature.login.repository;

import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.entity.Voting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VotingRepository extends JpaRepository<Voting, UUID> {
    Optional<Voting> findByUserAndBlog(User user, Blog blog);
}
