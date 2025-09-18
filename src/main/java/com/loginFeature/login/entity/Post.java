package com.loginFeature.login.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String university;
    private PostStatus status;
    private Boolean isApproved;
    private Boolean isAnonymous;
    private Integer upvotes;
    private Integer downvotes;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum PostStatus {
        PENDING, APPROVED, REJECTED
    }
}
