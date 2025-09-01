package com.loginFeature.login.repository;

import com.loginFeature.login.entity.SystemSettings;
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
public class SystemSettingsRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<SystemSettings> settingsRowMapper = new RowMapper<SystemSettings>() {
        @Override
        public SystemSettings mapRow(ResultSet rs, int rowNum) throws SQLException {
            SystemSettings settings = new SystemSettings();
            settings.setId(rs.getLong("id"));
            settings.setSettingKey(rs.getString("setting_key"));
            settings.setSettingValue(rs.getString("setting_value"));
            settings.setDescription(rs.getString("description"));
            settings.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return settings;
        }
    };
    
    public SystemSettings save(SystemSettings settings) {
        String sql = "INSERT INTO system_settings (setting_key, setting_value, description, updated_at) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, settings.getSettingKey());
            ps.setString(2, settings.getSettingValue());
            ps.setString(3, settings.getDescription());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        
        settings.setId(keyHolder.getKey().longValue());
        return settings;
    }
    
    public Optional<SystemSettings> findByKey(String key) {
        String sql = "SELECT * FROM system_settings WHERE setting_key = ?";
        try {
            SystemSettings settings = jdbcTemplate.queryForObject(sql, settingsRowMapper, key);
            return Optional.ofNullable(settings);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public List<SystemSettings> findAll() {
        String sql = "SELECT * FROM system_settings ORDER BY setting_key";
        return jdbcTemplate.query(sql, settingsRowMapper);
    }
    
    public void updateByKey(String key, String value) {
        String sql = "UPDATE system_settings SET setting_value = ?, updated_at = ? WHERE setting_key = ?";
        jdbcTemplate.update(sql, value, LocalDateTime.now(), key);
    }
    
    public void deleteByKey(String key) {
        String sql = "DELETE FROM system_settings WHERE setting_key = ?";
        jdbcTemplate.update(sql, key);
    }
    
    public boolean existsByKey(String key) {
        String sql = "SELECT COUNT(*) FROM system_settings WHERE setting_key = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, key);
        return count != null && count > 0;
    }
}
