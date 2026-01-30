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

        if (conn == null) {
            System.err.println("ERROR: Database connection is null!");
            return null;
        }

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

                System.out.println("✓ Map info loaded: " + mapData.getMapName());
                System.out.println("  Screen: " + mapData.getScreenWidth() + "x" + mapData.getScreenHeight());
            } else {
                System.err.println("ERROR: No map found with ID: " + mapId);
                mapRs.close();
                mapStmt.close();
                return null;
            }
            mapRs.close();
            mapStmt.close();

            // Load platforms
            String platformQuery = "SELECT * FROM platforms WHERE map_id = ?";
            PreparedStatement platformStmt = conn.prepareStatement(platformQuery);
            platformStmt.setInt(1, mapId);
            ResultSet platformRs = platformStmt.executeQuery();

            int platformCount = 0;
            while (platformRs.next()) {
                String platformType = platformRs.getString("platform_type");
                PlatformData platform = new PlatformData(
                        platformRs.getDouble("x"),
                        platformRs.getDouble("y"),
                        platformRs.getDouble("width"),
                        platformRs.getDouble("height"),
                        platformType,
                        platformType.equals("GROUND")
                );
                mapData.addPlatform(platform);
                platformCount++;

                System.out.println("  Platform " + platformCount + ": (" +
                        platform.getX() + ", " + platform.getY() + ") " +
                        platform.getWidth() + "x" + platform.getHeight() + " [" +
                        platform.getPlatformType() + "]");
            }
            System.out.println("✓ Loaded " + platformCount + " platforms");
            platformRs.close();
            platformStmt.close();

            // Load hazards (former obstacles)
            String hazardQuery = "SELECT * FROM hazards WHERE map_id = ?";
            PreparedStatement hazardStmt = conn.prepareStatement(hazardQuery);
            hazardStmt.setInt(1, mapId);
            ResultSet hazardRs = hazardStmt.executeQuery();

            int hazardCount = 0;
            while (hazardRs.next()) {
                ObstacleData hazard = new ObstacleData(
                        hazardRs.getDouble("x"),
                        hazardRs.getDouble("y"),
                        hazardRs.getDouble("width"),
                        hazardRs.getDouble("height"),
                        hazardRs.getString("hazard_type")
                );
                mapData.addObstacle(hazard);
                hazardCount++;
            }
            System.out.println("✓ Loaded " + hazardCount + " hazards");
            hazardRs.close();
            hazardStmt.close();

            // Load enemies
            String enemyQuery = "SELECT * FROM enemies WHERE map_id = ?";
            PreparedStatement enemyStmt = conn.prepareStatement(enemyQuery);
            enemyStmt.setInt(1, mapId);
            ResultSet enemyRs = enemyStmt.executeQuery();

            int enemyCount = 0;
            while (enemyRs.next()) {
                EnemyData enemy = new EnemyData(
                        enemyRs.getDouble("x"),
                        enemyRs.getDouble("y"),
                        enemyRs.getString("enemy_type")
                );
                mapData.addEnemy(enemy);
                enemyCount++;
            }
            System.out.println("✓ Loaded " + enemyCount + " enemies");
            enemyRs.close();
            enemyStmt.close();

            // Load items
            String itemQuery = "SELECT * FROM items WHERE map_id = ?";
            PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
            itemStmt.setInt(1, mapId);
            ResultSet itemRs = itemStmt.executeQuery();

            int itemCount = 0;
            while (itemRs.next()) {
                String itemType = itemRs.getString("item_type");

                // Chuyển đổi item type từ DB sang enum
                if (itemType.equals("HEALTH") || itemType.equals("COIN") ||
                        itemType.equals("BUFF") || itemType.equals("ABILITY")) {

                    ItemData item = new ItemData(
                            itemRs.getDouble("x"),
                            itemRs.getDouble("y"),
                            itemType.equals("BUFF") ? "DASH_BOOST" : itemType
                    );
                    mapData.addItem(item);
                    itemCount++;

                    System.out.println("  Item " + itemCount + ": " + itemType +
                            " at (" + item.getX() + ", " + item.getY() + ")");
                }
            }
            System.out.println("✓ Loaded " + itemCount + " items");
            itemRs.close();
            itemStmt.close();

            System.out.println("=== Map loaded successfully ===");

        } catch (SQLException e) {
            System.err.println("ERROR loading map data!");
            e.printStackTrace();
            return null;
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
            String query = "INSERT INTO maps (map_name, screen_width, screen_height) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, mapName);
            stmt.setDouble(2, screenWidth);
            stmt.setDouble(3, screenHeight);

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
                                      double width, double height, String type) {
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "INSERT INTO platforms (map_id, x, y, width, height, platform_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, mapId);
            stmt.setDouble(2, x);
            stmt.setDouble(3, y);
            stmt.setDouble(4, width);
            stmt.setDouble(5, height);
            stmt.setString(6, type);

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
     * Thêm item vào map
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