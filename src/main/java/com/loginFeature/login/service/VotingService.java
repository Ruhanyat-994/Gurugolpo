package com.loginFeature.login.service;

import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.entity.Voting;
import com.loginFeature.login.enums.VoteType;
import com.loginFeature.login.repository.BlogRepository;
import com.loginFeature.login.repository.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.server.UID;
import java.util.Optional;
import java.util.UUID;

@Service
public class VotingService {

    @Autowired
    private VotingRepository votingRepository;

    @Autowired
    private BlogRepository blogRepository;

    public String vote(UUID blogId, User user, VoteType voteType){
        Blog blog = blogRepository.findById(blogId).orElseThrow(()-> new RuntimeException("Blog Not Found"));

        Optional<Voting> existingBlog = votingRepository.findByUserAndBlog(user, blog);

        if(existingBlog.isPresent()){
            Voting voting = existingBlog.get();

            if(voting.getVoteType() == voteType){
                votingRepository.delete(voting);
                return "Vote Withdrawn";
            }
            voting.setVoteType(voteType);
            votingRepository.save(voting);
            return "Vote changed to " + voteType;
        }else{
            Voting voting = new Voting();
            voting.setBlog(blog);
            voting.setUser(user);
            voting.setVoteType(voteType);
            votingRepository.save(voting);
            return "Voted " + voteType;
        }

    }
}
