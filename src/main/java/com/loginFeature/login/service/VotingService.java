package com.loginFeature.login.service;

import com.loginFeature.login.entity.Comment;
import com.loginFeature.login.entity.Post;
import com.loginFeature.login.entity.PostVote;
import com.loginFeature.login.entity.CommentVote;
import com.loginFeature.login.enums.VoteType;
import com.loginFeature.login.repository.PostRepository;
import com.loginFeature.login.repository.CommentRepository;
import com.loginFeature.login.repository.PostVoteRepository;
import com.loginFeature.login.repository.CommentVoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class VotingService {

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private CommentVoteRepository commentVoteRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public String voteOnPost(Long postId, Long userId, VoteType voteType) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }

        Post post = postOpt.get();
        Optional<PostVote> existingVote = postVoteRepository.findByPostIdAndUserId(postId, userId);

        if (existingVote.isPresent()) {
            PostVote vote = existingVote.get();
            if (vote.getVoteType() == voteType) {
                // Same vote type - remove the vote
                postVoteRepository.deleteByPostIdAndUserId(postId, userId);
                updatePostVoteCounts(post);
                return "Vote withdrawn";
            } else {
                // Different vote type - change the vote
                vote.setVoteType(voteType);
                postVoteRepository.update(vote);
                updatePostVoteCounts(post);
                return "Vote changed to " + voteType;
            }
        } else {
            // New vote
            PostVote vote = new PostVote();
            vote.setPostId(postId);
            vote.setUserId(userId);
            vote.setVoteType(voteType);
            postVoteRepository.save(vote);
            updatePostVoteCounts(post);
            return "Voted " + voteType;
        }
    }

    public String voteOnComment(Long commentId, Long userId, VoteType voteType) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            throw new IllegalArgumentException("Comment not found");
        }

        Comment comment = commentOpt.get();
        Optional<CommentVote> existingVote = commentVoteRepository.findByCommentIdAndUserId(commentId, userId);

        if (existingVote.isPresent()) {
            CommentVote vote = existingVote.get();
            if (vote.getVoteType() == voteType) {
                // Same vote type - remove the vote
                commentVoteRepository.deleteByCommentIdAndUserId(commentId, userId);
                updateCommentVoteCounts(comment);
                return "Vote withdrawn";
            } else {
                // Different vote type - change the vote
                vote.setVoteType(voteType);
                commentVoteRepository.update(vote);
                updateCommentVoteCounts(comment);
                return "Vote changed to " + voteType;
            }
        } else {
            // New vote
            CommentVote vote = new CommentVote();
            vote.setCommentId(commentId);
            vote.setUserId(userId);
            vote.setVoteType(voteType);
            commentVoteRepository.save(vote);
            updateCommentVoteCounts(comment);
            return "Voted " + voteType;
        }
    }

    public Map<String, Long> getPostVoteCount(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }

        Post post = postOpt.get();
        long upvotes = postVoteRepository.countByPostIdAndVoteType(postId, VoteType.UPVOTE);
        long downvotes = postVoteRepository.countByPostIdAndVoteType(postId, VoteType.DOWNVOTE);

        Map<String, Long> voteCount = new HashMap<>();
        voteCount.put("upvotes", upvotes);
        voteCount.put("downvotes", downvotes);
        voteCount.put("totalVotes", upvotes + downvotes);
        voteCount.put("voteCount", (long) post.getVoteCount());
        return voteCount;
    }

    public Map<String, Long> getCommentVoteCount(Long commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            throw new IllegalArgumentException("Comment not found");
        }

        Comment comment = commentOpt.get();
        long upvotes = commentVoteRepository.countByCommentIdAndVoteType(commentId, VoteType.UPVOTE);
        long downvotes = commentVoteRepository.countByCommentIdAndVoteType(commentId, VoteType.DOWNVOTE);

        Map<String, Long> voteCount = new HashMap<>();
        voteCount.put("upvotes", upvotes);
        voteCount.put("downvotes", downvotes);
        voteCount.put("totalVotes", upvotes + downvotes);
        voteCount.put("voteCount", (long) comment.getVoteCount());
        return voteCount;
    }

    private void updatePostVoteCounts(Post post) {
        long upvotes = postVoteRepository.countByPostIdAndVoteType(post.getId(), VoteType.UPVOTE);
        long downvotes = postVoteRepository.countByPostIdAndVoteType(post.getId(), VoteType.DOWNVOTE);

        post.setUpvotes((int) upvotes);
        post.setDownvotes((int) downvotes);
        post.setVoteCount((int) (upvotes - downvotes)); // Net vote count
        postRepository.update(post);
    }

    private void updateCommentVoteCounts(Comment comment) {
        long upvotes = commentVoteRepository.countByCommentIdAndVoteType(comment.getId(), VoteType.UPVOTE);
        long downvotes = commentVoteRepository.countByCommentIdAndVoteType(comment.getId(), VoteType.DOWNVOTE);

        comment.setUpvotes((int) upvotes);
        comment.setDownvotes((int) downvotes);
        comment.setVoteCount((int) (upvotes - downvotes)); // Net vote count
        commentRepository.update(comment);
    }
}