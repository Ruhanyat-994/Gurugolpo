package com.loginFeature.login.repository;

import com.loginFeature.login.entity.University;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UniversityRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<University> universityRowMapper = (rs, rowNum) -> {
        University university = new University();
        university.setId(rs.getLong("id"));
        university.setName(rs.getString("name"));
        university.setDescription(rs.getString("description"));
        university.setIsActive(rs.getBoolean("is_active"));
        university.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        university.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return university;
    };
    
    public List<University> findAllActive() {
        String sql = "SELECT * FROM universities WHERE is_active = true ORDER BY name";
        return jdbcTemplate.query(sql, universityRowMapper);
    }
    
    public Optional<University> findById(Long id) {
        String sql = "SELECT * FROM universities WHERE id = ? AND is_active = true";
        List<University> results = jdbcTemplate.query(sql, universityRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public Optional<University> findByName(String name) {
        String sql = "SELECT * FROM universities WHERE name = ? AND is_active = true";
        List<University> results = jdbcTemplate.query(sql, universityRowMapper, name);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public University save(University university) {
        if (university.getId() == null) {
            // Insert new university
            String sql = "INSERT INTO universities (name, description, is_active, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
            jdbcTemplate.update(sql, university.getName(), university.getDescription(), university.getIsActive());
            
            // Get the generated ID
            String getIdSql = "SELECT LAST_INSERT_ID()";
            Long id = jdbcTemplate.queryForObject(getIdSql, Long.class);
            university.setId(id);
        } else {
            // Update existing university
            String sql = "UPDATE universities SET name = ?, description = ?, is_active = ?, updated_at = NOW() WHERE id = ?";
            jdbcTemplate.update(sql, university.getName(), university.getDescription(), university.getIsActive(), university.getId());
        }
        return university;
    }
}
