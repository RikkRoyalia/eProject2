-- Tạo database
CREATE DATABASE shaif_game;
USE shaif_game;

-- Bảng maps
CREATE TABLE maps (
    map_id INT PRIMARY KEY AUTO_INCREMENT,
    map_name VARCHAR(100) NOT NULL,
    screen_width DOUBLE NOT NULL,
    screen_height DOUBLE NOT NULL,
    ground_level DOUBLE NOT NULL,
    goal_x DOUBLE NOT NULL,
    goal_y DOUBLE NOT NULL,
    goal_width DOUBLE DEFAULT 15,
    goal_height DOUBLE DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng platforms
CREATE TABLE platforms (
    platform_id INT PRIMARY KEY AUTO_INCREMENT,
    map_id INT NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    width DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    platform_type ENUM('normal', 'moving', 'breakable') DEFAULT 'normal',
    FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- Bảng obstacles (pits, spikes, etc.)
CREATE TABLE obstacles (
    obstacle_id INT PRIMARY KEY AUTO_INCREMENT,
    map_id INT NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    width DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    obstacle_type ENUM('pit', 'spike', 'wall') DEFAULT 'pit',
    FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- Bảng enemies
CREATE TABLE enemies (
    enemy_id INT PRIMARY KEY AUTO_INCREMENT,
    map_id INT NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    enemy_type VARCHAR(50) DEFAULT 'shooter',
    FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- Insert sample data
INSERT INTO maps (map_name, screen_width, screen_height, ground_level, goal_x, goal_y) 
VALUES ('Level 1', 1920, 1080, 1040, 1870, 680);

INSERT INTO platforms (map_id, x, y, width, height, platform_type) VALUES
(1, 550, 180, 150, 20, 'normal'),
(1, 600, 260, 150, 20, 'normal'),
(1, 550, 340, 150, 20, 'normal'),
(1, 600, 420, 150, 20, 'normal'),
(1, 800, 500, 150, 20, 'normal'),
(1, 750, 580, 150, 20, 'normal');

INSERT INTO obstacles (map_id, x, y, width, height, obstacle_type) VALUES
(1, 300, 840, 80, 200, 'pit'),
(1, 600, 790, 100, 250, 'pit');

INSERT INTO enemies (map_id, x, y, enemy_type) VALUES
(1, 600, 300, 'shooter');