package com.loginFeature.login.service;

import com.loginFeature.login.Dto.UserRegistrationDto;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        User newUser = new User();
        newUser.setEmail(registrationDto.getEmail());
        newUser.setFullName(registrationDto.getFullName());
        newUser.setUsername(registrationDto.getEmail()); // Use email as username
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setUniversity(registrationDto.getUniversity());
        newUser.setRole(User.UserRole.USER);
        newUser.setIsActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(newUser);
    }

    public User createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getUsersByUniversity(String university) {
        return userRepository.findByUniversity(university);
    }

    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.update(user);
        return user;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public User promoteToModerator(Long userId, String university) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(User.UserRole.MODERATOR);
            user.setUniversity(university);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.update(user);
            return user;
        }
        throw new IllegalArgumentException("User not found");
    }
    
    public User promoteToAdmin(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(User.UserRole.ADMIN);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.update(user);
            return user;
        }
        throw new IllegalArgumentException("User not found");
    }

    // Additional methods for web controller
    public long getModeratorCount() {
        return userRepository.countByRole(User.UserRole.MODERATOR);
    }

    public long getAdminCount() {
        return userRepository.countByRole(User.UserRole.ADMIN);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    public List<User> getInactiveUsers() {
        return userRepository.findByIsActive(false);
    }
}
