package com.loginFeature.login.repository;

import com.loginFeature.login.entity.Post;
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
public class PostRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Post> postRowMapper = new RowMapper<Post>() {
        @Override
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            Post post = new Post();
            post.setId(rs.getLong("id"));
            post.setTitle(rs.getString("title"));
            post.setContent(rs.getString("content"));
            post.setAuthorId(rs.getLong("author_id"));
            post.setUniversity(rs.getString("university"));
            post.setStatus(Post.PostStatus.valueOf(rs.getString("status")));
            post.setIsApproved(rs.getBoolean("is_approved"));
            post.setUpvotes(rs.getInt("upvotes"));
            post.setDownvotes(rs.getInt("downvotes"));
            post.setVoteCount(rs.getInt("vote_count"));
            post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return post;
        }
    };
    
    public Post save(Post post) {
        String sql = "INSERT INTO posts (title, content, author_id, university, status, is_approved, upvotes, downvotes, vote_count, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setLong(3, post.getAuthorId());
            ps.setString(4, post.getUniversity());
            ps.setString(5, post.getStatus().name());
            ps.setBoolean(6, post.getIsApproved());
            ps.setInt(7, post.getUpvotes());
            ps.setInt(8, post.getDownvotes());
            ps.setInt(9, post.getVoteCount());
            ps.setTimestamp(10, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(11, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        
        post.setId(keyHolder.getKey().longValue());
        return post;
    }
    
    public Optional<Post> findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        try {
            Post post = jdbcTemplate.queryForObject(sql, postRowMapper, id);
            return Optional.ofNullable(post);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public List<Post> findAll() {
        String sql = "SELECT * FROM posts ORDER BY vote_count DESC, created_at DESC";
        return jdbcTemplate.query(sql, postRowMapper);
    }
    
    public List<Post> findAllOrderByVoteCountDesc() {
        String sql = "SELECT * FROM posts WHERE is_approved = true ORDER BY vote_count DESC, created_at DESC";
        return jdbcTemplate.query(sql, postRowMapper);
    }
    
    public List<Post> findByUniversity(String university) {
        String sql = "SELECT * FROM posts WHERE university = ? AND is_approved = true ORDER BY vote_count DESC, created_at DESC";
        return jdbcTemplate.query(sql, postRowMapper, university);
    }
    
    public List<Post> findByAuthorId(Long authorId) {
        String sql = "SELECT * FROM posts WHERE author_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, postRowMapper, authorId);
    }
    
    public List<Post> findByStatus(Post.PostStatus status) {
        String sql = "SELECT * FROM posts WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, postRowMapper, status.name());
    }
    
    public List<Post> searchByKeyWord(String keyword) {
        String sql = "SELECT * FROM posts WHERE (LOWER(title) LIKE LOWER(?) OR content LIKE ?) AND is_approved = true ORDER BY vote_count DESC, created_at DESC";
        String searchPattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, postRowMapper, searchPattern, searchPattern);
    }
    
    public List<Post> findPendingPosts() {
        String sql = "SELECT * FROM posts WHERE status = 'PENDING' ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, postRowMapper);
    }
    
    public List<Post> findPendingPostsByUniversity(String university) {
        String sql = "SELECT * FROM posts WHERE status = 'PENDING' AND university = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, postRowMapper, university);
    }
    
    public void update(Post post) {
        String sql = "UPDATE posts SET title = ?, content = ?, status = ?, is_approved = ?, upvotes = ?, downvotes = ?, vote_count = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getContent(), post.getStatus().name(), 
                           post.getIsApproved(), post.getUpvotes(), post.getDownvotes(), 
                           post.getVoteCount(), LocalDateTime.now(), post.getId());
    }
    
    public void deleteById(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    public long count() {
        String sql = "SELECT COUNT(*) FROM posts";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    public long countByUniversity(String university) {
        String sql = "SELECT COUNT(*) FROM posts WHERE university = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, university);
    }
    
    public List<Post> findTopVotedPosts(int limit) {
        String sql = "SELECT * FROM posts WHERE is_approved = true ORDER BY vote_count DESC, created_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, postRowMapper, limit);
    }
}
