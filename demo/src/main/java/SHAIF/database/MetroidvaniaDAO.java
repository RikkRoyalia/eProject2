package SHAIF.database;

import SHAIF.controller.AbilityPickup;
import SHAIF.controller.SavePoint;
import SHAIF.model.RoomConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO classes cho Metroidvania features
 */
public class MetroidvaniaDAO {

    /**
     * Load room connections từ database
     */
    public static List<RoomConnection> loadRoomConnections(int mapId) {
        List<RoomConnection> connections = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT * FROM room_connections WHERE from_map_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, mapId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RoomConnection connection = new RoomConnection(
                        rs.getString("direction"),
                        String.valueOf(rs.getInt("to_map_id")), // Convert to room_id string
                        rs.getDouble("connection_x"),
                        rs.getDouble("connection_y"),
                        rs.getDouble("spawn_x"),
                        rs.getDouble("spawn_y")
                );
                connections.add(connection);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error loading room connections: " + e.getMessage());
            e.printStackTrace();
        }

        return connections;
    }

    /**
     * Load ability pickups cho một room
     */
    public static List<AbilityPickup> loadAbilityPickups(int mapId) {
        List<AbilityPickup> pickups = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT * FROM ability_pickups WHERE map_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, mapId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AbilityPickup pickup = new AbilityPickup(
                        rs.getString("ability_id"),
                        rs.getDouble("x"),
                        rs.getDouble("y")
                );
                pickups.add(pickup);
            }

            rs.close();
            stmt.close();

            System.out.println("Loaded " + pickups.size() + " ability pickups for map " + mapId);

        } catch (SQLException e) {
            System.err.println("Error loading ability pickups: " + e.getMessage());
            e.printStackTrace();
        }

        return pickups;
    }

    /**
     * Load save points cho một room
     */
    public static List<SavePoint> loadSavePoints(int mapId) {
        List<SavePoint> savePoints = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT * FROM save_points WHERE map_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, mapId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Get room_id from maps table
                String roomId = getRoomIdFromMapId(mapId);

                SavePoint savePoint = new SavePoint(
                        roomId,
                        rs.getDouble("x"),
                        rs.getDouble("y")
                );
                savePoints.add(savePoint);
            }

            rs.close();
            stmt.close();

            System.out.println("Loaded " + savePoints.size() + " save points for map " + mapId);

        } catch (SQLException e) {
            System.err.println("Error loading save points: " + e.getMessage());
            e.printStackTrace();
        }

        return savePoints;
    }

    /**
     * Get room_id from map_id
     */
    private static String getRoomIdFromMapId(int mapId) {
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT room_id FROM maps WHERE map_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, mapId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String roomId = rs.getString("room_id");
                rs.close();
                stmt.close();
                return roomId != null ? roomId : "room_" + mapId;
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error getting room_id: " + e.getMessage());
        }

        return "room_" + mapId;
    }

    /**
     * Load tất cả rooms từ database để build WorldMap
     */
    public static List<RoomData> loadAllRooms() {
        List<RoomData> rooms = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT * FROM maps WHERE room_id IS NOT NULL ORDER BY map_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                RoomData roomData = new RoomData();
                roomData.setMapId(rs.getInt("map_id"));
                roomData.setRoomId(rs.getString("room_id"));
                roomData.setRoomName(rs.getString("room_name"));
                roomData.setWorldX(rs.getDouble("world_x"));
                roomData.setWorldY(rs.getDouble("world_y"));
                roomData.setScreenWidth(rs.getDouble("screen_width"));
                roomData.setScreenHeight(rs.getDouble("screen_height"));
                roomData.setRequiredAbility(rs.getString("required_ability"));

                rooms.add(roomData);
            }

            rs.close();
            stmt.close();

            System.out.println("Loaded " + rooms.size() + " rooms from database");

        } catch (SQLException e) {
            System.err.println("Error loading rooms: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }

    /**
     * Mark ability pickup as collected
     */
    public static void markAbilityCollected(int mapId, String abilityId) {
        // For now, this is stored in save file
        // Could also be stored in database if needed
        System.out.println("Ability collected: " + abilityId + " in map " + mapId);
    }

    /**
     * Load permanent changes (broken walls, activated switches)
     */
    public static List<PermanentChange> loadPermanentChanges(int mapId) {
        List<PermanentChange> changes = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT * FROM permanent_changes WHERE map_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, mapId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PermanentChange change = new PermanentChange(
                        rs.getString("change_key"),
                        rs.getString("change_type"),
                        rs.getDouble("x"),
                        rs.getDouble("y")
                );
                changes.add(change);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Error loading permanent changes: " + e.getMessage());
            e.printStackTrace();
        }

        return changes;
    }

    /**
     * Save permanent change
     */
    public static void savePermanentChange(int mapId, String changeKey,
                                           String changeType, double x, double y) {
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "INSERT INTO permanent_changes (map_id, change_key, change_type, x, y) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE change_type = VALUES(change_type)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, mapId);
            stmt.setString(2, changeKey);
            stmt.setString(3, changeType);
            stmt.setDouble(4, x);
            stmt.setDouble(5, y);

            stmt.executeUpdate();
            stmt.close();

            System.out.println("Saved permanent change: " + changeKey);

        } catch (SQLException e) {
            System.err.println("Error saving permanent change: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * RoomData - Data class for room info
 */
class RoomData {
    private int mapId;
    private String roomId;
    private String roomName;
    private double worldX;
    private double worldY;
    private double screenWidth;
    private double screenHeight;
    private String requiredAbility;

    // Getters and Setters
    public int getMapId() { return mapId; }
    public void setMapId(int mapId) { this.mapId = mapId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public double getWorldX() { return worldX; }
    public void setWorldX(double worldX) { this.worldX = worldX; }

    public double getWorldY() { return worldY; }
    public void setWorldY(double worldY) { this.worldY = worldY; }

    public double getScreenWidth() { return screenWidth; }
    public void setScreenWidth(double screenWidth) { this.screenWidth = screenWidth; }

    public double getScreenHeight() { return screenHeight; }
    public void setScreenHeight(double screenHeight) { this.screenHeight = screenHeight; }

    public String getRequiredAbility() { return requiredAbility; }
    public void setRequiredAbility(String requiredAbility) {
        this.requiredAbility = requiredAbility;
    }
}

/**
 * PermanentChange - Thay đổi vĩnh viễn trong world
 */
class PermanentChange {
    private String changeKey;
    private String changeType;
    private double x;
    private double y;

    public PermanentChange(String changeKey, String changeType, double x, double y) {
        this.changeKey = changeKey;
        this.changeType = changeType;
        this.x = x;
        this.y = y;
    }

    // Getters
    public String getChangeKey() { return changeKey; }
    public String getChangeType() { return changeType; }
    public double getX() { return x; }
    public double getY() { return y; }
}