package com.loginFeature.login.service;

import com.loginFeature.login.Dto.ModeratorDashboardDto;
import com.loginFeature.login.Dto.PostDto;
import com.loginFeature.login.entity.Post;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.PostRepository;
import com.loginFeature.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeratorService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    public ModeratorDashboardDto getDashboardData(String university) {
        ModeratorDashboardDto dashboard = new ModeratorDashboardDto();
        
        dashboard.setAssignedUniversity(university);
        
        // Get pending posts for the moderator's university
        List<Post> pendingPosts = postRepository.findPendingPostsByUniversity(university);
        dashboard.setPendingPosts(postService.convertToDtoList(pendingPosts));
        dashboard.setPendingCount((long) pendingPosts.size());
        
        // Get approved and rejected counts
        List<Post> allPosts = postRepository.findByUniversity(university);
        long approvedCount = allPosts.stream()
                .filter(post -> post.getStatus() == Post.PostStatus.APPROVED)
                .count();
        long rejectedCount = allPosts.stream()
                .filter(post -> post.getStatus() == Post.PostStatus.REJECTED)
                .count();
        
        dashboard.setApprovedCount(approvedCount);
        dashboard.setRejectedCount(rejectedCount);
        
        return dashboard;
    }

    public Post approvePost(Long postId, String moderatorUniversity) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        
        // Verify the post belongs to the moderator's university
        if (!post.getUniversity().equals(moderatorUniversity)) {
            throw new IllegalArgumentException("You can only moderate posts from your assigned university");
        }
        
        return postService.approvePost(postId);
    }

    public Post rejectPost(Long postId, String moderatorUniversity) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        
        // Verify the post belongs to the moderator's university
        if (!post.getUniversity().equals(moderatorUniversity)) {
            throw new IllegalArgumentException("You can only moderate posts from your assigned university");
        }
        
        return postService.rejectPost(postId);
    }

    public void deletePost(Long postId, String moderatorUniversity) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        
        // Verify the post belongs to the moderator's university
        if (!post.getUniversity().equals(moderatorUniversity)) {
            throw new IllegalArgumentException("You can only delete posts from your assigned university");
        }
        
        postRepository.deleteById(postId);
    }
}
