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
    ('Underground Cave',1280,720,4000,0,'cave','Underground Cave'),
    ('Crystal Cavern',1280,720,-2000,0,'crystal_cavern','Crystal Cavern'),
    ('Ancient Temple',1280,720,-4000,0,'temple','Ancient Temple'),
    ('Boss Chamber',1280,720,6000,0,'boss_room','Boss Chamber');

-- ===============================
-- PLATFORMS (STARTING AREA)
-- ===============================
INSERT INTO platforms (map_id, x, y, width, height, platform_type) VALUES
    (1, 0, 680, 1280, 40, 'GROUND'),
    -- to the right
    (1, 640, 600, 60, 20, 'STATIC'),
    (1, 675, 565, 60, 20, 'STATIC'),
    (1, 710, 530, 60, 20, 'STATIC'),
    (1, 745, 495, 60, 20, 'STATIC'),
    (1, 780, 460, 60, 20, 'STATIC'),
    (1, 815, 425, 60, 20, 'STATIC'),
    (1, 850, 390, 430, 20, 'STATIC'),
        -- house
    (1, 1040, 290, 120, 100, 'STATIC'),
    -- save point
    (1, 1040, 600, 100, 20, 'STATIC'),
    -- to the left
    (1, 0, 600, 60, 20, 'STATIC'),
    (1, 35, 565, 60, 20, 'STATIC'),
    (1, 70, 530, 200, 20, 'STATIC'),
        -- house
    (1, 100, 480, 50, 50, 'STATIC'),


    -- Ground and edges
    (2, 0, 680, 1280, 40, 'GROUND'),
    (2, 0, 0, 10, 540, 'STATIC'),
    (2, 1270, 280, 10, 400, 'STATIC'),
    -- Attach to ground
    (2, 900, 660, 20, 20, 'STATIC'),
    -- Attach to right edge
    (2, 1180, 520, 90, 40, 'STATIC'),
    (2, 1160, 520, 20, 20, 'STATIC'),
    (2, 1190, 390, 80, 50, 'STATIC'),
    (2, 1140, 210, 140, 90, 'STATIC'),
    -- To the right
    (2, 1050, 440, 80, 30, 'STATIC'),
    (2, 1050, 590, 80, 30, 'STATIC'),
    -- Attach to left edge
        -- Upper
    (2,   10, 80, 150, 30, 'STATIC'),
    (2,  160, 95, 100, 90, 'STATIC'),
    (2,  260, 125, 80, 30, 'STATIC'),
    (2,  340, 140, 80, 30, 'STATIC'),
        -- Between
    (2,   0, 260, 1040, 50, 'STATIC'),
    (2, 1040, 350,   20, 20, 'STATIC'),
        -- Lower
    (2,   10, 420, 120, 30, 'STATIC'),
    (2,   140, 485, 120, 30, 'STATIC'),
    (2,  300, 530, 120, 30, 'STATIC'),
    -- Middle
    (2,  680, 0, 100, 110, 'STATIC'),
    (2,  715, 110, 30, 150, 'BREAKABLE'),
    (2,  460, 180, 80, 30, 'STATIC'),
    (2,  480, 510, 500, 50, 'STATIC'),
    (2,  980, 510, 20, 20, 'STATIC'),


    -- ground and edges
    (3, 0, 680, 1280, 40, 'GROUND'),
    (3, 0, 60, 10, 620, 'STATIC'),
    (3, 1270, 0, 10, 340, 'STATIC'),
    -- attach to the right
    (3, 980, 530, 300, 150, 'STATIC'),
    (3, 950, 560, 30, 20, 'STATIC'),
    (3, 900, 640, 80, 40, 'STATIC'),
    (3, 850, 660, 50, 20, 'STATIC'),
    (3, 1050, 300, 220, 40, 'STATIC'),
    (3, 1030, 80, 240, 40, 'STATIC'),
    (3, 990, 80, 40, 15, 'STATIC'),
    -- attach to the left
    (3, 10, 60, 130, 40, 'STATIC'),
    (3, 10, 210, 210, 50, 'STATIC'),
    (3, 10, 410, 850, 80, 'STATIC'),
    (3, 10, 490, 170, 50, 'STATIC'),
    (3, 860, 470, 30, 20, 'STATIC'),
    (3, 400, 360, 280, 50, 'STATIC'),
    (3, 570, 320, 90, 40, 'STATIC'),
    -- middle
    (3, 260, 130, 120, 30, 'STATIC'),
    (3, 280, 270, 120, 30, 'STATIC'),
    (3, 480, 70, 120, 30, 'STATIC'),
    (3, 690, 240, 120, 30, 'STATIC'),
    (3, 840, 160, 120, 30, 'STATIC'),


    -- ground and edges
    (4, 0, 680, 190, 40, 'GROUND'),
    (4, 490, 680, 990, 40, 'GROUND'),
    (4, 1270, 0, 10, 680, 'STATIC'),
    (4, 0, 140, 10, 460, 'STATIC'),
    (4, 480, 0, 1000, 50, 'STATIC'),
    -- attach to ground
    (4, 850, 580, 45, 100, 'STATIC'),
    (4, 820, 570, 150, 20, 'STATIC'),
    (4, 730, 620, 120, 20, 'STATIC'),
    (4, 680, 595, 50, 45, 'STATIC'),
    (4, 1190, 630, 50, 50, 'STATIC'),
    -- attach to left
    (4, 10, 140, 20, 20, 'STATIC'),
    (4, 10, 160, 60, 20, 'STATIC'),
    (4, 10, 180, 570, 30, 'STATIC'),
    (4, 10, 340, 250, 30, 'STATIC'),
    (4, 10, 460, 875, 45, 'STATIC'),
    (4, 10, 580, 130, 20, 'STATIC'),
    (4, 10, 210, 30, 130, 'BREAKABLE'),


    (5, 0, 680, 1280, 40, 'GROUND'),


    (6, 0, 680, 1280, 40, 'GROUND');

-- ===============================
-- SAVE POINTS
-- ===============================
INSERT INTO save_points (map_id, x, y, name) VALUES
    (1, 1100,560, 'default-savepoint'),
    (2, 500,640, 'dark-forest-savepoint'),
    (3, 80, 180, 'underground-cave-savepoint');

-- ===============================
-- HAZARDS
-- ===============================
INSERT INTO hazards (map_id, x, y, width, height, hazard_type) VALUES
    (1, 990, 680, 200, 40, 'PIT'),
    (1,0, 680, 250, 40, 'LAVA'),

    (2, 300, 680, 80, 40, 'PIT'),
    (2, 600, 680, 100, 40, 'PIT'),

    (3, 10, 410, 120, 40, 'LAVA'),

    (3, 110, 650, 30, 30, 'SPIKE'),
    (3, 140, 650, 30, 30, 'SPIKE'),
    (3, 170, 650, 30, 30, 'SPIKE'),
    (3, 200, 650, 30, 30, 'SPIKE'),

    (3, 300, 650, 30, 30, 'SPIKE'),
    (3, 330, 650, 30, 30, 'SPIKE'),
    (3, 360, 650, 30, 30, 'SPIKE'),
    (3, 390, 650, 30, 30, 'SPIKE'),
    (3, 420, 650, 30, 30, 'SPIKE'),

    (3, 600, 650, 30, 30, 'SPIKE'),
    (3, 630, 650, 30, 30, 'SPIKE'),
    (3, 660, 650, 30, 30, 'SPIKE'),
    (3, 690, 650, 30, 30, 'SPIKE'),
    (3, 720, 650, 30, 30, 'SPIKE');

-- ===============================
-- ENEMIES
-- ===============================
INSERT INTO enemies (map_id, x, y, enemy_type) VALUES 
(2, 870, 640, 'SHOOTER'),
(2, 480, 470, 'PATROLLER'),
(2, 80, 220, 'CHASER'),

(3, 1240, 260, 'SHOOTER'),
(3, 590, 280, 'JUMPER');

-- ===============================
-- ITEMS
-- ===============================
INSERT INTO items (map_id, x, y, item_type, ability_id) VALUES
    -- Attach to ground
    (2, 1000, 650, 'HEALTH', NULL),
    (2, 150, 650, 'COIN', NULL),
    (2, 180, 650, 'COIN', NULL),
    (2, 210, 650, 'COIN', NULL),
    -- Middle
    (2, 670, 480, 'COIN', NULL),
    (2, 700, 480, 'COIN', NULL),
    (2, 730, 480, 'COIN', NULL),
    (2, 760, 480, 'COIN', NULL),
    (2, 790, 480, 'COIN', NULL),
    -- Attach to left edge
        -- Between
    (2, 55, 230, 'COIN', NULL),
    (2, 85, 230, 'COIN', NULL),
    (2, 115, 230, 'COIN', NULL),
    (2, 55, 200, 'COIN', NULL),
    (2, 85, 200, 'COIN', NULL),
    (2, 115, 200, 'COIN', NULL),
    (2, 55, 170, 'COIN', NULL),
    (2, 85, 170, 'COIN', NULL),
    (2, 115, 170, 'COIN', NULL),
        -- Lower
    (2, 200, 455, 'COIN', NULL),
    (2, 360, 500, 'COIN', NULL),
    (2, 60, 390, 'ABILITY', 'double_jump'),

    (3, 70, 360, 'ABILITY', 'wall_jump'),
    (3, 750, 210, 'COIN', 'NULL'),
    (3, 900, 120, 'COIN', 'NULL'),
    (3, 930, 90, 'COIN', 'NULL'),
    (3, 960, 60, 'COIN', 'NULL'),
    (3, 1240, 50, 'ABILITY', 'ground_pound'),
    (3, 570, 650, 'COIN', 'NULL'),
    (3, 540, 650, 'COIN', 'NULL'),
    (3, 510, 650, 'COIN', 'NULL'),
    (3, 480, 650, 'COIN', 'NULL'),
    (3, 800, 380, 'HEALTH', 'NULL'),
    (3, 30, 650, 'COIN', 'NULL'),
    (3, 60, 650, 'COIN', 'NULL'),
    (3, 90, 650, 'COIN', 'NULL'),
    (3, 30, 620, 'COIN', 'NULL'),
    (3, 60, 620, 'COIN', 'NULL'),
    (3, 90, 620, 'COIN', 'NULL'),
    (3, 30, 590, 'COIN', 'NULL'),
    (3, 60, 590, 'COIN', 'NULL'),
    (3, 90, 590, 'COIN', 'NULL');

-- ===============================
-- ROOM CONNECTIONS (EDGE BASED)
-- ===============================
INSERT INTO room_connections
(from_map_id, to_map_id, edge, spawn_x, spawn_y, required_ability)
VALUES
    (1, 2, 'RIGHT', 15, 650, NULL),
    (2, 1, 'LEFT', 1265, 650, NULL),
    (2, 3, 'RIGHT', 15, 30, NULL),
    (3, 2, 'LEFT', 1265, 180, NULL),
    (1, 4, 'LEFT', 1265, 650, NULL),
    (4, 1, 'RIGHT', 15, 650, NULL),
    (4, 5, 'LEFT', 1265, 650, NULL),
    (5, 4, 'RIGHT', 15, 650, NULL),
    (5, 6, 'LEFT', 1265, 650, NULL),
    (6, 5, 'RIGHT', 15, 650, NULL);

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
