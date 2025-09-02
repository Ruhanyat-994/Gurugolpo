package com.loginFeature.login.repository;

import com.loginFeature.login.entity.CommentVote;
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
public class CommentVoteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<CommentVote> rowMapper = new RowMapper<CommentVote>() {
        @Override
        public CommentVote mapRow(ResultSet rs, int rowNum) throws SQLException {
            CommentVote vote = new CommentVote();
            vote.setId(rs.getLong("id"));
            vote.setCommentId(rs.getLong("comment_id"));
            vote.setUserId(rs.getLong("user_id"));
            vote.setVoteType(VoteType.valueOf(rs.getString("vote_type")));
            vote.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return vote;
        }
    };

    public CommentVote save(CommentVote vote) {
        String sql = "INSERT INTO comment_votes (comment_id, user_id, vote_type, created_at) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, vote.getCommentId());
            ps.setLong(2, vote.getUserId());
            ps.setString(3, vote.getVoteType().name());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

        vote.setId(keyHolder.getKey().longValue());
        return vote;
    }
    
    public void update(CommentVote vote) {
        String sql = "UPDATE comment_votes SET vote_type = ? WHERE id = ?";
        jdbcTemplate.update(sql, vote.getVoteType().name(), vote.getId());
    }

    public Optional<CommentVote> findByCommentIdAndUserId(Long commentId, Long userId) {
        String sql = "SELECT * FROM comment_votes WHERE comment_id = ? AND user_id = ?";
        try {
            CommentVote vote = jdbcTemplate.queryForObject(sql, rowMapper, commentId, userId);
            return Optional.ofNullable(vote);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void deleteByCommentIdAndUserId(Long commentId, Long userId) {
        String sql = "DELETE FROM comment_votes WHERE comment_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, commentId, userId);
    }

    public long countByCommentIdAndVoteType(Long commentId, VoteType voteType) {
        String sql = "SELECT COUNT(*) FROM comment_votes WHERE comment_id = ? AND vote_type = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, commentId, voteType.name());
    }
}
