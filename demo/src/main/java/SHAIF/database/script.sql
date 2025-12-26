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
                         enemy_type VARCHAR(50) DEFAULT 'shooter',
                         FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- Insert sample data (không có ground_level trong maps)
INSERT INTO maps (map_name, screen_width, screen_height, goal_x, goal_y)
VALUES ('Level 1', 1280, 720, 1200, 600);

-- Insert GROUND platform (is_ground = TRUE)
-- Ground sẽ ở y = 680, chiều cao 40px
INSERT INTO platforms (map_id, x, y, width, height, platform_type, is_ground) VALUES
    (1, 0, 680, 1280, 40, 'ground', TRUE);

-- Insert các platforms thường
INSERT INTO platforms (map_id, x, y, width, height, platform_type, is_ground) VALUES
                                                                                  (1, 550, 180, 150, 20, 'normal', FALSE),
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