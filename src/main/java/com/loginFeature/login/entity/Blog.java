package com.loginFeature.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String title;
    // @Column(columnDefinition = "TEXT") // This will alow you to save large string in the mysql db or use LOB
    @Lob
    private String content;
    private String author;

}
