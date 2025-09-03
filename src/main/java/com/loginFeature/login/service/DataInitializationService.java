package com.loginFeature.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private PostService postService;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Sync vote counts for all existing posts
            postService.syncAllPostVoteCounts();
            System.out.println("Successfully synced vote counts for all posts");
        } catch (Exception e) {
            System.err.println("Failed to sync vote counts: " + e.getMessage());
        }
    }
}
