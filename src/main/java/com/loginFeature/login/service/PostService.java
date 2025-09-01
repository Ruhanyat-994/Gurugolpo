package com.loginFeature.login.service;

import com.loginFeature.login.Dto.PostCreateDto;
import com.loginFeature.login.Dto.PostDto;
import com.loginFeature.login.entity.Post;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.PostRepository;
import com.loginFeature.login.repository.UserRepository;
import com.loginFeature.login.repository.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    public Post createPost(PostCreateDto postDto, Long authorId) {
        Optional<User> userOpt = userRepository.findById(authorId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User user = userOpt.get();
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setAuthorId(authorId);
        post.setUniversity(user.getUniversity());
        
        // Check if post management is enabled
        boolean postManagementEnabled = isPostManagementEnabled();
        if (postManagementEnabled) {
            post.setStatus(Post.PostStatus.PENDING);
            post.setIsApproved(false);
        } else {
            post.setStatus(Post.PostStatus.APPROVED);
            post.setIsApproved(true);
        }
        
        post.setUpvotes(0);
        post.setDownvotes(0);
        post.setVoteCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        
        return postRepository.save(post);
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllOrderByVoteCountDesc();
    }

    public List<Post> getPostsByUniversity(String university) {
        return postRepository.findByUniversity(university);
    }

    public List<Post> getPostsByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    public List<Post> searchPosts(String keyword) {
        return postRepository.searchByKeyWord(keyword);
    }

    public List<Post> getPendingPosts() {
        return postRepository.findPendingPosts();
    }

    public List<Post> getPendingPostsByUniversity(String university) {
        return postRepository.findPendingPostsByUniversity(university);
    }

    public Post updatePost(Post post) {
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.update(post);
        return post;
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public Post approvePost(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            post.setStatus(Post.PostStatus.APPROVED);
            post.setIsApproved(true);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.update(post);
            return post;
        }
        throw new IllegalArgumentException("Post not found");
    }

    public Post rejectPost(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            post.setStatus(Post.PostStatus.REJECTED);
            post.setIsApproved(false);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.update(post);
            return post;
        }
        throw new IllegalArgumentException("Post not found");
    }

    public List<Post> getTopVotedPosts(int limit) {
        return postRepository.findTopVotedPosts(limit);
    }

    public long getPostCount() {
        return postRepository.count();
    }

    public long getPostCountByUniversity(String university) {
        return postRepository.countByUniversity(university);
    }

    private boolean isPostManagementEnabled() {
        Optional<com.loginFeature.login.entity.SystemSettings> setting = 
            systemSettingsRepository.findByKey("post_management_enabled");
        return setting.map(s -> Boolean.parseBoolean(s.getSettingValue())).orElse(false);
    }

    public PostDto convertToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorId(post.getAuthorId());
        dto.setUniversity(post.getUniversity());
        dto.setStatus(post.getStatus().name());
        dto.setIsApproved(post.getIsApproved());
        dto.setUpvotes(post.getUpvotes());
        dto.setDownvotes(post.getDownvotes());
        dto.setVoteCount(post.getVoteCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        
        // Get author username
        Optional<User> author = userRepository.findById(post.getAuthorId());
        dto.setAuthorUsername(author.map(User::getUsername).orElse("Unknown"));
        
        return dto;
    }

    public List<PostDto> convertToDtoList(List<Post> posts) {
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}