package com.loginFeature.login.repository;

import com.loginFeature.login.entity.PostVote;
import com.loginFeature.login.enums.VoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PostVoteRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<PostVote> postVoteRowMapper = new RowMapper<PostVote>() {
        @Override
        public PostVote mapRow(ResultSet rs, int rowNum) throws SQLException {
            PostVote vote = new PostVote();
            vote.setId(rs.getLong("id"));
            vote.setPostId(rs.getLong("post_id"));
            vote.setUserId(rs.getLong("user_id"));
            vote.setVoteType(VoteType.valueOf(rs.getString("vote_type")));
            vote.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return vote;
        }
    };
    
    public PostVote save(PostVote vote) {
        String sql = "INSERT INTO post_votes (post_id, user_id, vote_type, created_at) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, vote.getPostId());
            ps.setLong(2, vote.getUserId());
            ps.setString(3, vote.getVoteType().name());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        
        vote.setId(keyHolder.getKey().longValue());
        return vote;
    }
    
    public Optional<PostVote> findByPostIdAndUserId(Long postId, Long userId) {
        String sql = "SELECT * FROM post_votes WHERE post_id = ? AND user_id = ?";
        try {
            PostVote vote = jdbcTemplate.queryForObject(sql, postVoteRowMapper, postId, userId);
            return Optional.ofNullable(vote);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public void deleteByPostIdAndUserId(Long postId, Long userId) {
        String sql = "DELETE FROM post_votes WHERE post_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, postId, userId);
    }
    
    public long countByPostIdAndVoteType(Long postId, VoteType voteType) {
        String sql = "SELECT COUNT(*) FROM post_votes WHERE post_id = ? AND vote_type = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, postId, voteType.name());
    }
    
    public void deleteById(Long id) {
        String sql = "DELETE FROM post_votes WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}