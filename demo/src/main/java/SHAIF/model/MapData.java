package SHAIF.model;

import java.util.ArrayList;
import java.util.List;

public class MapData {
    private int mapId;
    private String mapName;
    private double screenWidth;
    private double screenHeight;
    private double goalX;
    private double goalY;
    private double goalWidth;
    private double goalHeight;

    private List<PlatformData> platforms;
    private List<ObstacleData> obstacles;
    private List<EnemyData> enemies;

    public MapData() {
        platforms = new ArrayList<>();
        obstacles = new ArrayList<>();
        enemies = new ArrayList<>();
    }

    // Getters and Setters
    public int getMapId() { return mapId; }
    public void setMapId(int mapId) { this.mapId = mapId; }

    public String getMapName() { return mapName; }
    public void setMapName(String mapName) { this.mapName = mapName; }

    public double getScreenWidth() { return screenWidth; }
    public void setScreenWidth(double screenWidth) { this.screenWidth = screenWidth; }

    public double getScreenHeight() { return screenHeight; }
    public void setScreenHeight(double screenHeight) { this.screenHeight = screenHeight; }

    public double getGoalX() { return goalX; }
    public void setGoalX(double goalX) { this.goalX = goalX; }

    public double getGoalY() { return goalY; }
    public void setGoalY(double goalY) { this.goalY = goalY; }

    public double getGoalWidth() { return goalWidth; }
    public void setGoalWidth(double goalWidth) { this.goalWidth = goalWidth; }

    public double getGoalHeight() { return goalHeight; }
    public void setGoalHeight(double goalHeight) { this.goalHeight = goalHeight; }

    public List<PlatformData> getPlatforms() { return platforms; }
    public void setPlatforms(List<PlatformData> platforms) { this.platforms = platforms; }

    public List<ObstacleData> getObstacles() { return obstacles; }
    public void setObstacles(List<ObstacleData> obstacles) { this.obstacles = obstacles; }

    public List<EnemyData> getEnemies() { return enemies; }
    public void setEnemies(List<EnemyData> enemies) { this.enemies = enemies; }

    public void addPlatform(PlatformData platform) {
        platforms.add(platform);
    }

    public void addObstacle(ObstacleData obstacle) {
        obstacles.add(obstacle);
    }

    public void addEnemy(EnemyData enemy) {
        enemies.add(enemy);
    }

    // Method mới: lấy ground level từ platform có is_ground = true
    public double getGroundLevel() {
        for (PlatformData platform : platforms) {
            if (platform.isGround()) {
                return platform.getY();
            }
        }
        // Fallback nếu không tìm thấy ground platform
        return screenHeight - 40;
    }
}