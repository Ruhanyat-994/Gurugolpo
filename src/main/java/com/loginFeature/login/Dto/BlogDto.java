package com.loginFeature.login.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogDto {
    private UUID id;
    private String title;
    private String content;
    private String author;
    private List<CommentDto> comments;
    private long upVote;
    private long downVote;
}