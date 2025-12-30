-- Tạo database
CREATE DATABASE IF NOT EXISTS shaif_game;
USE shaif_game;

-- Bảng maps (bỏ ground_level vì sẽ lưu trong platforms)
CREATE TABLE maps (
    map_id INT PRIMARY KEY AUTO_INCREMENT,
    map_name VARCHAR(100) NOT NULL,
    screen_width DOUBLE NOT NULL,
    screen_height DOUBLE NOT NULL,
    goal_x DOUBLE NOT NULL,
    goal_y DOUBLE NOT NULL,
    goal_width DOUBLE DEFAULT 15,
    goal_height DOUBLE DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng platforms (thêm is_ground để đánh dấu platform nào là ground)
CREATE TABLE platforms (
    platform_id INT PRIMARY KEY AUTO_INCREMENT,
    map_id INT NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    width DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    platform_type ENUM('normal', 'moving', 'breakable', 'ground') DEFAULT 'normal',
    is_ground BOOLEAN DEFAULT FALSE,
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
    enemy_type ENUM('SHOOTER', 'PATROLLER', 'JUMPER', 'CHASER', 'BOSS') DEFAULT 'SHOOTER',
    FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- Bảng items (MỚI)
CREATE TABLE items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    map_id INT NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    item_type ENUM('HEALTH', 'COIN', 'DASH_BOOST', 'SHIELD', 'SPEED_BOOST', 'DOUBLE_JUMP') NOT NULL,
    FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- Insert sample data cho Level 1
INSERT INTO maps (map_name, screen_width, screen_height, goal_x, goal_y)
VALUES ('Level 1', 1280, 720, 1200, 600);

-- Ground platform
INSERT INTO platforms (map_id, x, y, width, height, platform_type, is_ground) VALUES
    (1, 0, 680, 1280, 40, 'ground', TRUE);

-- Normal platforms
INSERT INTO platforms (map_id, x, y, width, height, platform_type, is_ground) VALUES
    (1, 550, 180, 150, 20, 'normal', FALSE),
    (1, 600, 260, 150, 20, 'normal', FALSE),
    (1, 550, 340, 150, 20, 'normal', FALSE),
    (1, 600, 420, 150, 20, 'normal', FALSE),
    (1, 800, 500, 150, 20, 'normal', FALSE),
    (1, 750, 600, 150, 20, 'normal', FALSE);

-- Obstacles (pits)
INSERT INTO obstacles (map_id, x, y, width, height, obstacle_type) VALUES
    (1, 300, 680, 80, 40, 'pit'),
    (1, 600, 680, 100, 50, 'pit');

-- Enemies
INSERT INTO enemies (map_id, x, y, enemy_type) VALUES
    (1, 600, 300, 'SHOOTER');

-- Items (MỚI)
INSERT INTO items (map_id, x, y, item_type) VALUES
    -- Health items
    (1, 550, 170, 'HEALTH'),
    (1, 650, 250, 'HEALTH'),
    -- Coins
    (1, 400, 650, 'COIN'),
    (1, 700, 650, 'COIN'),
    (1, 600, 330, 'COIN'),
    (1, 650, 410, 'COIN'),
    (1, 850, 490, 'COIN'),
    -- Power-ups
    (1, 800, 570, 'DASH_BOOST'),
    (1, 550, 330, 'SHIELD'),
    (1, 750, 490, 'SPEED_BOOST'),
    (1, 600, 250, 'DOUBLE_JUMP');                                                                   (1, 550, 180, 150, 20, 'normal', FALSE),

    (1, 600, 260, 150, 20, 'normal', FALSE),
    (1, 550, 340, 150, 20, 'normal', FALSE),
    (1, 600, 420, 150, 20, 'normal', FALSE),
    (1, 800, 500, 150, 20, 'normal', FALSE),
    (1, 750, 600, 150, 20, 'normal', FALSE);

-- Insert obstacles (pits)
-- Pits phải ở y = 680 (cùng level với ground)
INSERT INTO obstacles (map_id, x, y, width, height, obstacle_type) VALUES
    (1, 300, 680, 80, 40, 'pit'),
    (1, 600, 680, 100, 50, 'pit');

-- Insert enemies
INSERT INTO enemies (map_id, x, y, enemy_type) VALUES
    (1, 600, 300, 'shooter');