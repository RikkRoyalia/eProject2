package SHAIF.database;

import SHAIF.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MapDAO {

    /**
     * Load map data từ database theo mapId
     */
    public static MapData loadMap(int mapId) {
        MapData mapData = new MapData();
        Connection conn = DatabaseConnection.getConnection();

        try {
            // Load map info
            String mapQuery = "SELECT * FROM maps WHERE map_id = ?";
            PreparedStatement mapStmt = conn.prepareStatement(mapQuery);
            mapStmt.setInt(1, mapId);
            ResultSet mapRs = mapStmt.executeQuery();

            if (mapRs.next()) {
                mapData.setMapId(mapRs.getInt("map_id"));
                mapData.setMapName(mapRs.getString("map_name"));
                mapData.setScreenWidth(mapRs.getDouble("screen_width"));
                mapData.setScreenHeight(mapRs.getDouble("screen_height"));
                mapData.setGoalX(mapRs.getDouble("goal_x"));
                mapData.setGoalY(mapRs.getDouble("goal_y"));
                mapData.setGoalWidth(mapRs.getDouble("goal_width"));
                mapData.setGoalHeight(mapRs.getDouble("goal_height"));
            }
            mapRs.close();
            mapStmt.close();

            // Load platforms
            String platformQuery = "SELECT * FROM platforms WHERE map_id = ?";
            PreparedStatement platformStmt = conn.prepareStatement(platformQuery);
            platformStmt.setInt(1, mapId);
            ResultSet platformRs = platformStmt.executeQuery();

            while (platformRs.next()) {
                PlatformData platform = new PlatformData(
                        platformRs.getDouble("x"),
                        platformRs.getDouble("y"),
                        platformRs.getDouble("width"),
                        platformRs.getDouble("height"),
                        platformRs.getString("platform_type"),
                        platformRs.getBoolean("is_ground")
                );
                mapData.addPlatform(platform);
            }
            platformRs.close();
            platformStmt.close();

            // Load obstacles
            String obstacleQuery = "SELECT * FROM obstacles WHERE map_id = ?";
            PreparedStatement obstacleStmt = conn.prepareStatement(obstacleQuery);
            obstacleStmt.setInt(1, mapId);
            ResultSet obstacleRs = obstacleStmt.executeQuery();

            while (obstacleRs.next()) {
                ObstacleData obstacle = new ObstacleData(
                        obstacleRs.getDouble("x"),
                        obstacleRs.getDouble("y"),
                        obstacleRs.getDouble("width"),
                        obstacleRs.getDouble("height"),
                        obstacleRs.getString("obstacle_type")
                );
                mapData.addObstacle(obstacle);
            }
            obstacleRs.close();
            obstacleStmt.close();

            // Load enemies
            String enemyQuery = "SELECT * FROM enemies WHERE map_id = ?";
            PreparedStatement enemyStmt = conn.prepareStatement(enemyQuery);
            enemyStmt.setInt(1, mapId);
            ResultSet enemyRs = enemyStmt.executeQuery();

            while (enemyRs.next()) {
                EnemyData enemy = new EnemyData(
                        enemyRs.getDouble("x"),
                        enemyRs.getDouble("y"),
                        enemyRs.getString("enemy_type")
                );
                mapData.addEnemy(enemy);
            }
            enemyRs.close();
            enemyStmt.close();

            // Load items (MỚI)
            String itemQuery = "SELECT * FROM items WHERE map_id = ?";
            PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
            itemStmt.setInt(1, mapId);
            ResultSet itemRs = itemStmt.executeQuery();

            while (itemRs.next()) {
                ItemData item = new ItemData(
                        itemRs.getDouble("x"),
                        itemRs.getDouble("y"),
                        itemRs.getString("item_type")
                );
                mapData.addItem(item);
            }
            itemRs.close();
            itemStmt.close();

            System.out.println("Map '" + mapData.getMapName() + "' loaded successfully!");
            System.out.println("- Platforms: " + mapData.getPlatforms().size());
            System.out.println("- Obstacles: " + mapData.getObstacles().size());
            System.out.println("- Enemies: " + mapData.getEnemies().size());
            System.out.println("- Items: " + mapData.getItems().size());

        } catch (SQLException e) {
            System.err.println("Error loading map data!");
            e.printStackTrace();
        }

        return mapData;
    }

    /**
     * Lấy danh sách tất cả maps
     */
    public static List<MapData> getAllMaps() {
        List<MapData> maps = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT map_id, map_name FROM maps ORDER BY map_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                MapData mapData = new MapData();
                mapData.setMapId(rs.getInt("map_id"));
                mapData.setMapName(rs.getString("map_name"));
                maps.add(mapData);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error getting maps list!");
            e.printStackTrace();
        }

        return maps;
    }

    /**
     * Tạo map mới trong database
     */
    public static int createMap(String mapName, double screenWidth, double screenHeight,
                                double goalX, double goalY) {
        Connection conn = DatabaseConnection.getConnection();
        int mapId = -1;

        try {
            String query = "INSERT INTO maps (map_name, screen_width, screen_height, " +
                    "goal_x, goal_y) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, mapName);
            stmt.setDouble(2, screenWidth);
            stmt.setDouble(3, screenHeight);
            stmt.setDouble(4, goalX);
            stmt.setDouble(5, goalY);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    mapId = rs.getInt(1);
                    System.out.println("Map created with ID: " + mapId);
                }
                rs.close();
            }

            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error creating map!");
            e.printStackTrace();
        }

        return mapId;
    }

    /**
     * Thêm platform vào map
     */
    public static boolean addPlatform(int mapId, double x, double y,
                                      double width, double height, String type, boolean isGround) {
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "INSERT INTO platforms (map_id, x, y, width, height, platform_type, is_ground) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, mapId);
            stmt.setDouble(2, x);
            stmt.setDouble(3, y);
            stmt.setDouble(4, width);
            stmt.setDouble(5, height);
            stmt.setString(6, type);
            stmt.setBoolean(7, isGround);

            int affected = stmt.executeUpdate();
            stmt.close();

            return affected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding platform!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm item vào map (MỚI)
     */
    public static boolean addItem(int mapId, double x, double y, String itemType) {
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "INSERT INTO items (map_id, x, y, item_type) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, mapId);
            stmt.setDouble(2, x);
            stmt.setDouble(3, y);
            stmt.setString(4, itemType);

            int affected = stmt.executeUpdate();
            stmt.close();

            return affected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding item!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa map
     */
    public static boolean deleteMap(int mapId) {
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "DELETE FROM maps WHERE map_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, mapId);

            int affected = stmt.executeUpdate();
            stmt.close();

            return affected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting map!");
            e.printStackTrace();
            return false;
        }
    }
}