package com.loginFeature.login.service;

import com.loginFeature.login.Dto.CommentCreateDto;
import com.loginFeature.login.Dto.CommentDto;
import com.loginFeature.login.entity.Comment;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.CommentRepository;
import com.loginFeature.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Comment addComment(Long postId, CommentCreateDto commentDto, Long authorId) {
        Optional<User> userOpt = userRepository.findById(authorId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(authorId);
        comment.setContent(commentDto.getContent());
        comment.setUpvotes(0);
        comment.setDownvotes(0);
        comment.setVoteCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, CommentCreateDto commentDto, Long authorId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            throw new IllegalArgumentException("Comment not found");
        }

        Comment comment = commentOpt.get();
        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("Unauthorized: you can only edit your own comments!");
        }

        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.update(comment);
        return comment;
    }

    public void deleteComment(Long commentId, Long authorId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            throw new IllegalArgumentException("Comment not found");
        }

        Comment comment = commentOpt.get();
        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("Unauthorized: you can only delete your own comments!");
        }

        commentRepository.deleteById(commentId);
    }

    public List<Comment> getCommentsForPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public List<Comment> getCommentsByAuthor(Long authorId) {
        return commentRepository.findByAuthorId(authorId);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public long getCommentCount() {
        return commentRepository.count();
    }

    public long getCommentCountByPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setContent(comment.getContent());
        dto.setAuthorId(comment.getAuthorId());
        dto.setUpvotes(comment.getUpvotes());
        dto.setDownvotes(comment.getDownvotes());
        dto.setVoteCount(comment.getVoteCount());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        
        // Get author username
        Optional<User> author = userRepository.findById(comment.getAuthorId());
        dto.setAuthorUsername(author.map(User::getUsername).orElse("Unknown"));
        
        return dto;
    }

    public List<CommentDto> convertToDtoList(List<Comment> comments) {
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
