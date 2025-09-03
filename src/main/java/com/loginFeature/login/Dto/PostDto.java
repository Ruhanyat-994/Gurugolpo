package com.loginFeature.login.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorUsername;
    private String authorName; // For display purposes
    private String university;
    private String status;
    private Boolean isApproved;
    private Integer upvotes;
    private Integer downvotes;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for frontend
    private String sentiment;
    private Boolean verified;
    private Integer commentCount;
    private Boolean aiRewritten;
}