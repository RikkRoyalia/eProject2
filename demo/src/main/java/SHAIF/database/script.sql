-- ===============================
-- RESET DATABASE
-- ===============================
DROP DATABASE IF EXISTS shaif_game;
CREATE DATABASE shaif_game;
USE shaif_game;

-- ===============================
-- MAPS (NO GOAL)
-- ===============================
CREATE TABLE maps (
                      map_id INT PRIMARY KEY AUTO_INCREMENT,
                      map_name VARCHAR(100) NOT NULL,
                      screen_width DOUBLE NOT NULL,
                      screen_height DOUBLE NOT NULL,
                      world_x DOUBLE DEFAULT 0,
                      world_y DOUBLE DEFAULT 0,
                      room_id VARCHAR(50) UNIQUE,
                      room_name VARCHAR(100),
                      required_ability VARCHAR(50) NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- PLATFORMS
-- ===============================
CREATE TABLE platforms (
                           platform_id INT PRIMARY KEY AUTO_INCREMENT,
                           map_id INT NOT NULL,
                           x DOUBLE NOT NULL,
                           y DOUBLE NOT NULL,
                           width DOUBLE NOT NULL,
                           height DOUBLE NOT NULL,
                           platform_type ENUM('GROUND','STATIC','MOVING','BREAKABLE') DEFAULT 'STATIC',
                           FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- ===============================
-- HAZARDS (FORMER OBSTACLES)
-- ===============================
CREATE TABLE hazards (
                         hazard_id INT PRIMARY KEY AUTO_INCREMENT,
                         map_id INT NOT NULL,
                         x DOUBLE NOT NULL,
                         y DOUBLE NOT NULL,
                         width DOUBLE NOT NULL,
                         height DOUBLE NOT NULL,
                         hazard_type ENUM('PIT','SPIKE','LAVA') DEFAULT 'PIT',
                         FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- ===============================
-- ENEMIES
-- ===============================
CREATE TABLE enemies (
                         enemy_id INT PRIMARY KEY AUTO_INCREMENT,
                         map_id INT NOT NULL,
                         x DOUBLE NOT NULL,
                         y DOUBLE NOT NULL,
                         enemy_type ENUM('SHOOTER','PATROLLER','JUMPER','CHASER','BOSS') DEFAULT 'SHOOTER',
                         FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- ===============================
-- ITEMS (ABILITY MERGED)
-- ===============================
CREATE TABLE items (
                       item_id INT PRIMARY KEY AUTO_INCREMENT,
                       map_id INT NOT NULL,
                       x DOUBLE NOT NULL,
                       y DOUBLE NOT NULL,
                       item_type ENUM('HEALTH','COIN','BUFF','ABILITY') NOT NULL,
                       ability_id VARCHAR(50),
                       FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- ===============================
-- ROOM CONNECTIONS (EDGE-BASED)
-- ===============================
CREATE TABLE room_connections (
                                  connection_id INT PRIMARY KEY AUTO_INCREMENT,
                                  from_map_id INT NOT NULL,
                                  to_map_id INT NOT NULL,
                                  edge ENUM('LEFT','RIGHT','TOP','BOTTOM') NOT NULL,
                                  spawn_x DOUBLE NOT NULL,
                                  spawn_y DOUBLE NOT NULL,
                                  required_ability VARCHAR(50),
                                  FOREIGN KEY (from_map_id) REFERENCES maps(map_id) ON DELETE CASCADE,
                                  FOREIGN KEY (to_map_id) REFERENCES maps(map_id) ON DELETE CASCADE,
                                  UNIQUE KEY unique_edge (from_map_id, edge)
);

-- ===============================
-- SAVE POINTS
-- ===============================
CREATE TABLE save_points (
                             save_point_id INT PRIMARY KEY AUTO_INCREMENT,
                             map_id INT NOT NULL,
                             x DOUBLE NOT NULL,
                             y DOUBLE NOT NULL,
                             name VARCHAR(100),
                             FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE
);

-- ===============================
-- PERMANENT CHANGES
-- ===============================
CREATE TABLE permanent_changes (
                                   change_id INT PRIMARY KEY AUTO_INCREMENT,
                                   map_id INT NOT NULL,
                                   change_key VARCHAR(100) NOT NULL,
                                   x DOUBLE NOT NULL,
                                   y DOUBLE NOT NULL,
                                   change_type ENUM('SWITCH','BROKEN_WALL','OPENED_DOOR') NOT NULL,
                                   FOREIGN KEY (map_id) REFERENCES maps(map_id) ON DELETE CASCADE,
                                   UNIQUE KEY unique_change (map_id, change_key)
);

-- ===============================
-- INSERT MAPS
-- ===============================
INSERT INTO maps (map_name, screen_width, screen_height, world_x, world_y, room_id, room_name)
VALUES
    ('Starting Area',1280,720,0,0,'starting_area','Starting Area'),
    ('Dark Forest',1280,720,2000,0,'forest','Dark Forest'),
    ('Underground Cave',1280,720,2000,1200,'cave','Underground Cave'),
    ('Crystal Cavern',1280,720,4000,1200,'crystal_cavern','Crystal Cavern'),
    ('Ancient Temple',1280,720,4500,0,'temple','Ancient Temple'),
    ('Boss Chamber',1500,1200,6000,0,'boss_room','Boss Chamber');

-- ===============================
-- PLATFORMS (STARTING AREA)
-- ===============================
INSERT INTO platforms (map_id, x, y, width, height, platform_type) VALUES
    (1, 0, 680, 1280, 40, 'GROUND'),
    (1, 550, 200, 150, 20, 'STATIC'),
    (1, 600, 280, 150, 20, 'STATIC'),
    (1, 550, 360, 150, 20, 'STATIC'),
    (1, 600, 440, 150, 20, 'STATIC'),

    (2, 0, 680, 1280, 40, 'GROUND'),
    (2, 550, 200, 150, 20, 'STATIC'),
    (2, 600, 280, 150, 20, 'STATIC'),
    (2, 550, 360, 150, 20, 'STATIC'),
    (2, 600, 440, 150, 20, 'STATIC');

-- ===============================
-- HAZARDS
-- ===============================
INSERT INTO hazards (map_id, x, y, width, height, hazard_type) VALUES
                                                                   (1, 300, 680, 80, 40, 'PIT'),
                                                                   (1, 600, 680, 100, 40, 'PIT');

-- ===============================
-- ENEMIES
-- ===============================
INSERT INTO enemies (map_id, x, y, enemy_type)
VALUES (1, 600, 300, 'SHOOTER');

-- ===============================
-- ITEMS
-- ===============================
INSERT INTO items (map_id, x, y, item_type, ability_id) VALUES
                                                            (1, 550, 170, 'HEALTH', NULL),
                                                            (1, 650, 250, 'HEALTH', NULL),
                                                            (1, 600, 250, 'ABILITY', 'double_jump');

-- ===============================
-- ROOM CONNECTIONS (EDGE BASED)
-- ===============================
INSERT INTO room_connections
(from_map_id, to_map_id, edge, spawn_x, spawn_y, required_ability)
VALUES
    (1, 2, 'RIGHT', 50, 600, NULL),
    (2, 3, 'BOTTOM', 600, 50, 'double_jump'),
    (2, 4, 'RIGHT', 50, 600, 'wall_jump'),
    (4, 6, 'RIGHT', 100, 900, 'charge_dash');

-- ===============================
-- VIEWS
-- ===============================
CREATE OR REPLACE VIEW v_world_map AS
SELECT
    m.map_id,
    m.room_id,
    m.room_name,
    m.world_x,
    m.world_y,
    m.screen_width,
    m.screen_height,
    COUNT(DISTINCT sp.save_point_id) AS save_point_count,
    COUNT(DISTINCT rc.connection_id) AS connection_count
FROM maps m
         LEFT JOIN save_points sp ON m.map_id = sp.map_id
         LEFT JOIN room_connections rc ON m.map_id = rc.from_map_id
GROUP BY m.map_id;

CREATE OR REPLACE VIEW v_room_connections AS
SELECT
    rc.connection_id,
    fm.room_id AS from_room,
    tm.room_id AS to_room,
    rc.edge,
    rc.spawn_x,
    rc.spawn_y,
    rc.required_ability
FROM room_connections rc
         JOIN maps fm ON rc.from_map_id = fm.map_id
         JOIN maps tm ON rc.to_map_id = tm.map_id;
