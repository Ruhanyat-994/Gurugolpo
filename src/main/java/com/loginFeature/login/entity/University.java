package com.loginFeature.login.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class University {
    
    private Long id;
    private String name;
    private String description;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
