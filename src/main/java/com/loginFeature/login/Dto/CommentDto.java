package com.loginFeature.login.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentDto {
    private UUID id;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
}
