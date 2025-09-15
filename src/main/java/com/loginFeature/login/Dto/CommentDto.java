package com.loginFeature.login.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long postId;
    private String content;
    private Long authorId;
    private String authorUsername;
    private String authorName; // For display purposes
    private Integer upvotes;
    private Integer downvotes;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
