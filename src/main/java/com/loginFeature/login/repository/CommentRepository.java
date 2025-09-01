package com.loginFeature.login.repository;

import com.loginFeature.login.entity.Comment;
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
import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Comment> commentRowMapper = new RowMapper<Comment>() {
        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comment comment = new Comment();
            comment.setId(rs.getLong("id"));
            comment.setPostId(rs.getLong("post_id"));
            comment.setAuthorId(rs.getLong("author_id"));
            comment.setContent(rs.getString("content"));
            comment.setUpvotes(rs.getInt("upvotes"));
            comment.setDownvotes(rs.getInt("downvotes"));
            comment.setVoteCount(rs.getInt("vote_count"));
            comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            comment.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return comment;
        }
    };
    
    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (post_id, author_id, content, upvotes, downvotes, vote_count, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, comment.getPostId());
            ps.setLong(2, comment.getAuthorId());
            ps.setString(3, comment.getContent());
            ps.setInt(4, comment.getUpvotes());
            ps.setInt(5, comment.getDownvotes());
            ps.setInt(6, comment.getVoteCount());
            ps.setTimestamp(7, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(8, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        
        comment.setId(keyHolder.getKey().longValue());
        return comment;
    }
    
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        try {
            Comment comment = jdbcTemplate.queryForObject(sql, commentRowMapper, id);
            return Optional.ofNullable(comment);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY vote_count DESC, created_at DESC";
        return jdbcTemplate.query(sql, commentRowMapper, postId);
    }
    
    public List<Comment> findByAuthorId(Long authorId) {
        String sql = "SELECT * FROM comments WHERE author_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, commentRowMapper, authorId);
    }
    
    public void update(Comment comment) {
        String sql = "UPDATE comments SET content = ?, upvotes = ?, downvotes = ?, vote_count = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, comment.getContent(), comment.getUpvotes(), 
                           comment.getDownvotes(), comment.getVoteCount(), 
                           LocalDateTime.now(), comment.getId());
    }
    
    public void deleteById(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    public long count() {
        String sql = "SELECT COUNT(*) FROM comments";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    public long countByPostId(Long postId) {
        String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, postId);
    }
}
