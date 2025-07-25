package com.loginFeature.login.controller;

import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.enums.VoteType;
import com.loginFeature.login.repository.UserRepsitory;
import com.loginFeature.login.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequestMapping("/api/blogs/vote")
public class VotingController {

    @Autowired
    private VotingService votingService;

    @Autowired
    private UserRepsitory userRepsitory;

    @PostMapping("/{blogId}/upvote")
    public ResponseEntity<?> upvote(@PathVariable UUID blogId, Authentication authentication){
        User user = userRepsitory.findByUsername(authentication.getName());
        String message = votingService.vote(blogId, user, VoteType.UPVOTE);
        return ResponseEntity.ok(message);
    }
    @PostMapping("/{blogId}/downvote")
    public ResponseEntity<?> downvote(@PathVariable UUID blogId, Authentication authentication){
        User user = userRepsitory.findByUsername(authentication.getName());
        String message = votingService.vote(blogId, user, VoteType.DOWNVOTE);
        return ResponseEntity.ok(message);
    }
    @GetMapping("/count/{blogId}")
    public ResponseEntity<Map<String, Long>> getVoteCounts(@PathVariable UUID blogId){
        return ResponseEntity.ok(votingService.getVoteCount(blogId));
    }

}