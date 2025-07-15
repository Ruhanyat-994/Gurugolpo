package com.loginFeature.login.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "blog_id")
    @JsonIgnore
    private Blog blog;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    private String content;
    @JsonProperty("createdAt")
    private LocalDateTime localDateTime = LocalDateTime.now();
}
