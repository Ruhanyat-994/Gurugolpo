package com.loginFeature.login.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loginFeature.login.enums.VoteType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    @JsonIgnore
    private List<Comment> comments = new LinkedList<>();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
    private List<Voting> votes = new ArrayList<>();

    public long getUpVote(){
        return votes.stream().filter(v-> v.getVoteType() == VoteType.UPVOTE).count();
    }
    public long getDownVote(){
        return votes.stream().filter(v-> v.getVoteType() == VoteType.DOWNVOTE).count();
    }

}
