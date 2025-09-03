-- Initial data for University Teacher Evaluation Forum

-- Insert default admin user (password: password) - ignore if already exists
INSERT IGNORE INTO users (email, username, password, university, role, is_active) VALUES
('admin@university.edu', 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System Admin', 'ADMIN', TRUE);   

-- Insert system settings - ignore if already exists
INSERT IGNORE INTO system_settings (setting_key, setting_value, description) VALUES
('post_management_enabled', 'false', 'Controls whether posts need approval before going live'),
('max_posts_per_day', '5', 'Maximum number of posts a student can create per day'),
('max_comments_per_post', '100', 'Maximum number of comments allowed per post');

-- Insert sample universities and users for testing - ignore if already exists
INSERT IGNORE INTO users (email, username, password, university, role, is_active) VALUES
('john.doe@mit.edu', 'johndoe', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'MIT', 'STUDENT', TRUE),
('jane.smith@stanford.edu', 'janesmith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Stanford University', 'STUDENT', TRUE),
('moderator@mit.edu', 'mit_moderator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'MIT', 'MODERATOR', TRUE),
('moderator@stanford.edu', 'stanford_moderator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Stanford University', 'MODERATOR', TRUE);
