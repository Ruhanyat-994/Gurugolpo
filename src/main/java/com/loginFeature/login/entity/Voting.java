package com.loginFeature.login.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.loginFeature.login.enums.VoteType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    @ManyToOne
    private User user;
    @ManyToOne
    @JsonBackReference
    private Blog blog;

}
