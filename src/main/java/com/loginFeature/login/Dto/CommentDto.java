package com.loginFeature.login.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CommentDto {
    private UUID id;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
}
