package com.loginFeature.login.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.loginFeature.login.enums.VoteType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

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

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Voting> votes = new HashSet<>();

    public long getUpVote(){
        return votes.stream().filter(v-> v.getVoteType() == VoteType.UPVOTE).count();
    }
    public long getDownVote(){
        return votes.stream().filter(v-> v.getVoteType() == VoteType.DOWNVOTE).count();
    }

}
