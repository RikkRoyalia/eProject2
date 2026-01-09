package SHAIF.controller;

import SHAIF.model.Player;
import SHAIF.model.RoomTransition;
import SHAIF.model.WorldMap;
import SHAIF.model.Room;
import SHAIF.model.RoomConnection;

/**
 * Phát hiện khi player đến mép màn hình để trigger room transition
 * FIXED: Now uses spawn positions from database instead of calculating them
 */
public class RoomTransitionDetector {
    private static final double TRANSITION_THRESHOLD = 30; // pixels từ mép màn hình

    private final WorldMap worldMap;
    private final double screenWidth;
    private final double screenHeight;

    public RoomTransitionDetector(WorldMap worldMap, double screenWidth, double screenHeight) {
        this.worldMap = worldMap;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    /**
     * Kiểm tra nếu player ở mép màn hình và có room connection
     * FIXED: Uses spawn_x, spawn_y from database instead of calculating
     */
    public RoomTransition checkTransition(Player player) {
        double playerX = player.getX();
        double playerY = player.getY();

        Room currentRoom = worldMap.getCurrentRoom();
        if (currentRoom == null) return null;

        // Xác định edge nào player đang chạm
        String edge = null;

        // LEFT edge
        if (playerX <= TRANSITION_THRESHOLD) {
            edge = "LEFT";
        }
        // RIGHT edge
        else if (playerX >= screenWidth - TRANSITION_THRESHOLD) {
            edge = "RIGHT";
        }
        // TOP edge
        else if (playerY <= TRANSITION_THRESHOLD) {
            edge = "TOP";
        }
        // BOTTOM edge
        else if (playerY >= screenHeight - TRANSITION_THRESHOLD) {
            edge = "BOTTOM";
        }

        // Nếu không ở mép nào
        if (edge == null) return null;

        // Tìm connection tương ứng với edge này
        for (RoomConnection conn : currentRoom.getConnections()) {
            if (conn.getDirection().equalsIgnoreCase(edge)) {
                // Tìm target room
                Room targetRoom = worldMap.getRooms().get(conn.getTargetRoomId());
                if (targetRoom != null) {
                    // ⭐ KEY FIX: Sử dụng spawn position TỪ DATABASE
                    // conn.getSpawnX() và conn.getSpawnY() là spawn_x, spawn_y từ room_connections table
                    double spawnX = conn.getSpawnX();
                    double spawnY = conn.getSpawnY();

                    System.out.println("\nTransition detected!");
                    System.out.println("  Edge: " + edge);
                    System.out.println("  Target: " + targetRoom.getName());
                    System.out.println("  Spawn from DB: (" + spawnX + ", " + spawnY + ")");

                    return new RoomTransition(
                            targetRoom.getId(),
                            spawnX,  // Từ database, không phải tính toán!
                            spawnY   // Từ database, không phải tính toán!
                    );
                }
            }
        }

        // Không có connection cho edge này
        return null;
    }

    /**
     * Kiểm tra và hiển thị indicator khi player gần mép có connection
     * Returns the direction of nearby connection (for UI hints)
     */
    public String getNearbyTransitionDirection(Player player) {
        double playerX = player.getX();
        double playerY = player.getY();
        double threshold = TRANSITION_THRESHOLD * 2;

        Room currentRoom = worldMap.getCurrentRoom();
        if (currentRoom == null) return null;

        // Check each edge and see if there's a connection
        if (playerX <= threshold) {
            for (RoomConnection conn : currentRoom.getConnections()) {
                if (conn.getDirection().equalsIgnoreCase("LEFT")) {
                    return "LEFT";
                }
            }
        }

        if (playerX >= screenWidth - threshold) {
            for (RoomConnection conn : currentRoom.getConnections()) {
                if (conn.getDirection().equalsIgnoreCase("RIGHT")) {
                    return "RIGHT";
                }
            }
        }

        if (playerY <= threshold) {
            for (RoomConnection conn : currentRoom.getConnections()) {
                if (conn.getDirection().equalsIgnoreCase("TOP")) {
                    return "TOP";
                }
            }
        }

        if (playerY >= screenHeight - threshold) {
            for (RoomConnection conn : currentRoom.getConnections()) {
                if (conn.getDirection().equalsIgnoreCase("BOTTOM")) {
                    return "BOTTOM";
                }
            }
        }

        return null;
    }

    /**
     * Simple check if player is near any edge with a connection
     */
    public boolean isNearTransition(Player player) {
        return getNearbyTransitionDirection(player) != null;
    }

    /**
     * Get info about a connection for display (e.g., "→ Dark Forest")
     */
    public String getTransitionInfo(Player player) {
        String direction = getNearbyTransitionDirection(player);
        if (direction == null) return null;

        Room currentRoom = worldMap.getCurrentRoom();
        if (currentRoom == null) return null;

        for (RoomConnection conn : currentRoom.getConnections()) {
            if (conn.getDirection().equalsIgnoreCase(direction)) {
                Room targetRoom = worldMap.getRooms().get(conn.getTargetRoomId());
                if (targetRoom != null) {
                    String arrow = getArrowForDirection(direction);
                    return arrow + " " + targetRoom.getName();
                }
            }
        }

        return null;
    }

    private String getArrowForDirection(String direction) {
        switch (direction.toUpperCase()) {
            case "LEFT": return "←";
            case "RIGHT": return "→";
            case "TOP": return "↑";
            case "BOTTOM": return "↓";
            default: return "";
        }
    }
}