package com.loginFeature.login.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BlogDto {
    private UUID id;
    private String title;
    private String content;
    private String author;
}