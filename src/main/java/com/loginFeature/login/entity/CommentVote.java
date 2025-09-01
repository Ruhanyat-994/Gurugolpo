package com.loginFeature.login.entity;

import com.loginFeature.login.enums.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVote {
    private Long id;
    private Long commentId;
    private Long userId;
    private VoteType voteType;
    private LocalDateTime createdAt;
}
