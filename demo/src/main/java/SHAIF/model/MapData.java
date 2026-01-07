package SHAIF.model;

import java.util.ArrayList;
import java.util.List;

public class MapData {
    private int mapId;
    private String mapName;
    private double screenWidth;
    private double screenHeight;

    private List<PlatformData> platforms;
    private List<ObstacleData> obstacles;
    private List<EnemyData> enemies;
    private List<ItemData> items;

    public MapData() {
        platforms = new ArrayList<>();
        obstacles = new ArrayList<>();
        enemies = new ArrayList<>();
        items = new ArrayList<>();
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

    public List<PlatformData> getPlatforms() { return platforms; }
    public void setPlatforms(List<PlatformData> platforms) { this.platforms = platforms; }

    public List<ObstacleData> getObstacles() { return obstacles; }
    public void setObstacles(List<ObstacleData> obstacles) { this.obstacles = obstacles; }

    public List<EnemyData> getEnemies() { return enemies; }
    public void setEnemies(List<EnemyData> enemies) { this.enemies = enemies; }

    public List<ItemData> getItems() { return items; }
    public void setItems(List<ItemData> items) { this.items = items; }

    public void addPlatform(PlatformData platform) {
        platforms.add(platform);
    }

    public void addObstacle(ObstacleData obstacle) {
        obstacles.add(obstacle);
    }

    public void addEnemy(EnemyData enemy) {
        enemies.add(enemy);
    }

    public void addItem(ItemData item) {
        items.add(item);
    }

    /**
     * Lấy ground level từ platform có type = GROUND
     */
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