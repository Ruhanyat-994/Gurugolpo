package com.loginFeature.login.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeratorDashboardDto {
    private String assignedUniversity;
    private List<PostDto> pendingPosts;
    private Long pendingCount;
    private Long approvedCount;
    private Long rejectedCount;
}
