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
     * FIXED: Sử dụng 'edge' thay vì 'direction', không có connection_x/y
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
                // Get target room ID from to_map_id
                String targetRoomId = getRoomIdFromMapId(rs.getInt("to_map_id"));

                // Database schema: edge, spawn_x, spawn_y (NO connection_x/y)
                RoomConnection connection = new RoomConnection(
                        rs.getString("edge"), // LEFT, RIGHT, TOP, BOTTOM
                        targetRoomId,
                        0, 0, // connection X,Y không dùng trong edge-based system
                        rs.getDouble("spawn_x"),
                        rs.getDouble("spawn_y")
                );
                connections.add(connection);

                System.out.println("    Connection: " + rs.getString("edge") +
                        " -> room " + targetRoomId +
                        " (spawn at " + rs.getDouble("spawn_x") + "," + rs.getDouble("spawn_y") + ")");
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
     * FIXED: Load từ items table với filter item_type = 'ABILITY'
     */
    public static List<AbilityPickup> loadAbilityPickups(int mapId) {
        List<AbilityPickup> pickups = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            // Items table có: item_type ENUM('HEALTH','COIN','BUFF','ABILITY')
            String query = "SELECT * FROM items WHERE map_id = ? AND item_type = 'ABILITY'";
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

                // Required ability có thể null
                String reqAbility = rs.getString("required_ability");
                roomData.setRequiredAbility(reqAbility);

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

