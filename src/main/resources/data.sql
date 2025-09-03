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

-- Insert sample posts for testing - ignore if already exists
INSERT IGNORE INTO posts (title, content, author_id, university, status, created_at, updated_at) VALUES
('Great Experience at Google Internship', 'I had an amazing time during my summer internship at Google. The team was supportive and I learned a lot about software engineering best practices. The work culture is fantastic and everyone is very collaborative.', 2, 'MIT', 'APPROVED', NOW(), NOW()),
('Challenging but Rewarding Experience at Amazon', 'Amazon was quite challenging but I learned a lot. The work pace is fast and expectations are high, but the learning opportunities are incredible. Would recommend for those who want to grow quickly.', 3, 'Stanford University', 'APPROVED', NOW(), NOW()),
('Microsoft Internship - Mixed Feelings', 'Microsoft has a great work-life balance and the people are friendly. However, the projects I worked on were not as challenging as I expected. Overall, it was a decent experience.', 2, 'MIT', 'APPROVED', NOW(), NOW()),
('Facebook/Meta - Intense but Valuable', 'The work at Meta is very intense and fast-paced. You need to be ready to work long hours, but the compensation and learning opportunities are top-notch. The company culture is very driven.', 3, 'Stanford University', 'APPROVED', NOW(), NOW()),
('Apple - Design-Focused Environment', 'Apple has an incredible focus on design and user experience. The attention to detail is amazing. However, the work can be quite secretive and you might not always know the bigger picture.', 2, 'MIT', 'APPROVED', NOW(), NOW());

-- Insert sample post votes for testing - ignore if already exists
INSERT IGNORE INTO post_votes (post_id, user_id, vote_type, created_at) VALUES
(1, 2, 'UPVOTE', NOW()),
(1, 3, 'UPVOTE', NOW()),
(1, 4, 'UPVOTE', NOW()),
(2, 2, 'UPVOTE', NOW()),
(2, 3, 'DOWNVOTE', NOW()),
(3, 2, 'UPVOTE', NOW()),
(3, 4, 'UPVOTE', NOW()),
(4, 2, 'UPVOTE', NOW()),
(4, 3, 'UPVOTE', NOW()),
(4, 4, 'UPVOTE', NOW()),
(5, 2, 'UPVOTE', NOW()),
(5, 3, 'UPVOTE', NOW());