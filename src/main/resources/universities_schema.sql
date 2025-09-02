-- Universities table schema
CREATE TABLE IF NOT EXISTS universities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default universities
INSERT INTO universities (name, description, is_active) VALUES
('United International University', 'Leading private university in Bangladesh', TRUE),
('University of Dhaka', 'Premier public university in Bangladesh', TRUE),
('Bangladesh University of Engineering and Technology', 'Top engineering university in Bangladesh', TRUE),
('Chittagong University', 'Leading university in Chittagong region', TRUE),
('Khulna University', 'Premier university in Khulna region', TRUE),
('Rajshahi University', 'Leading university in Rajshahi region', TRUE),
('Jahangirnagar University', 'Distinguished public university near Dhaka', TRUE),
('Shahjalal University of Science and Technology', 'Premier science and technology university', TRUE),
('Dhaka University of Engineering and Technology', 'Leading engineering institution in Dhaka', TRUE),
('Bangladesh Agricultural University', 'Premier agricultural university in Bangladesh', TRUE)
ON DUPLICATE KEY UPDATE name = name;
