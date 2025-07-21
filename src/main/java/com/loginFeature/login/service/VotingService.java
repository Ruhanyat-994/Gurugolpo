package com.loginFeature.login.service;

import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.entity.Voting;
import com.loginFeature.login.enums.VoteType;
import com.loginFeature.login.repository.BlogRepository;
import com.loginFeature.login.repository.VotingRepository;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.server.UID;
import java.util.*;

@Slf4j
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
                updateVoteCounts(blog);
                return "Vote Withdrawn";
            }
            voting.setVoteType(voteType);
            votingRepository.save(voting);
            updateVoteCounts(blog);
            return "Vote changed to " + voteType;
        }else{
            Voting voting = new Voting();
            voting.setBlog(blog);
            voting.setUser(user);
            voting.setVoteType(voteType);
            votingRepository.save(voting);
            updateVoteCounts(blog);
            return "Voted " + voteType;
        }
    }
    public Map<String, Long> getVoteCount(UUID blogId){
        Blog blog = blogRepository.findById(blogId).orElseThrow(()-> new RuntimeException("Blog Not Found"));

        long upvotes = votingRepository.countByBlogAndVoteType(blog,VoteType.UPVOTE);
        long downvotes = votingRepository.countByBlogAndVoteType(blog,VoteType.DOWNVOTE);

        Map<String, Long> voteCount = new HashMap<>();
        voteCount.put("upvotes",upvotes);
        voteCount.put("downvotes",downvotes);
        voteCount.put("totalVotes",upvotes+downvotes);
        return voteCount;

    }

    public List<Blog> getAllBlogsSortedByPopularity(){
        List<Blog> allBlogs = blogRepository.findAll();

        allBlogs.sort((b1,b2) -> Integer.compare(b2.getVoteCount(), b1.getVoteCount()));

        return allBlogs;
    }

    public void updateVoteCounts(Blog blog){
        long up = votingRepository.countByBlogAndVoteType(blog, VoteType.UPVOTE);
        long down = votingRepository.countByBlogAndVoteType(blog, VoteType.DOWNVOTE);

        blog.setUpVote(up);
        blog.setDownVote(down);
        blog.setVoteCount((int)(up+down));
        blogRepository.save(blog);
    }
}