package com.loginFeature.login.service;

import com.loginFeature.login.Dto.AdminDashboardDto;
import com.loginFeature.login.Dto.PostDto;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.UserRepository;
import com.loginFeature.login.repository.PostRepository;
import com.loginFeature.login.repository.CommentRepository;
import com.loginFeature.login.repository.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    @Autowired
    private PostService postService;

    public AdminDashboardDto getDashboardData() {
        AdminDashboardDto dashboard = new AdminDashboardDto();
        
        // Get top voted posts
        List<PostDto> topPosts = postService.convertToDtoList(postRepository.findTopVotedPosts(10));
        dashboard.setTopVotedPosts(topPosts);
        
        // Get post counts by university
        Map<String, Long> postCountsByUniversity = new HashMap<>();
        List<User> users = userRepository.findByRole(User.UserRole.USER);
        for (User user : users) {
            String university = user.getUniversity();
            long count = postRepository.countByUniversity(university);
            postCountsByUniversity.put(university, count);
        }
        dashboard.setPostCountsByUniversity(postCountsByUniversity);
        
        // Get system settings
        boolean postManagementEnabled = isPostManagementEnabled();
        dashboard.setPostManagementEnabled(postManagementEnabled);
        
        // Get total counts
        dashboard.setTotalPosts(postRepository.count());
        dashboard.setTotalUsers(userRepository.count());
        dashboard.setTotalComments(commentRepository.count());
        dashboard.setTotalModerators(userRepository.countByRole(User.UserRole.MODERATOR));
        
        return dashboard;
    }

    public void togglePostManagement(boolean enabled) {
        systemSettingsRepository.updateByKey("post_management_enabled", String.valueOf(enabled));
    }

    public User promoteUserToModerator(Long userId, String university) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setRole(User.UserRole.MODERATOR);
                    user.setUniversity(university);
                    userRepository.update(user);
                    return user;
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private boolean isPostManagementEnabled() {
        return systemSettingsRepository.findByKey("post_management_enabled")
                .map(setting -> Boolean.parseBoolean(setting.getSettingValue()))
                .orElse(false);
    }
}
