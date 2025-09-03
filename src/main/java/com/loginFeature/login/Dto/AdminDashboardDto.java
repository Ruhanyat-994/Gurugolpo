package com.loginFeature.login.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    private List<PostDto> topVotedPosts;
    private Map<String, Long> postCountsByUniversity;
    private Boolean postManagementEnabled;
    private Long totalPosts;
    private Long totalUsers;
    private Long totalComments;
    private Long totalModerators;
}
