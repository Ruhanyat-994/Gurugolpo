package com.loginFeature.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blog {
    @Id
    @GeneratedValue

    private UUID id;
    private String title;
    // @Column(columnDefinition = "TEXT") // This will alow you to save large string in the mysql db or use LOB
    @Lob
    private String content;
    private String author;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new LinkedList<>();

}
