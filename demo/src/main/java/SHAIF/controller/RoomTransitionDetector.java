package SHAIF.controller;

import SHAIF.model.Player;
import SHAIF.model.RoomTransition;
import SHAIF.model.WorldMap;
import SHAIF.model.Room;
import SHAIF.model.RoomConnection;

/**
 * Phát hiện khi player đến mép màn hình để trigger room transition
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
     */
    public RoomTransition checkTransition(Player player) {
        double playerX = player.getX();
        double playerY = player.getY();

        Room currentRoom = worldMap.getCurrentRoom();
        if (currentRoom == null) return null;

        // Kiểm tra từng edge
        String edge = null;
        double spawnX = 0;
        double spawnY = 0;

        // LEFT edge
        if (playerX <= TRANSITION_THRESHOLD) {
            edge = "LEFT";
            spawnX = screenWidth - 50; // Spawn ở bên phải của room mới
            spawnY = playerY;
        }
        // RIGHT edge
        else if (playerX >= screenWidth - TRANSITION_THRESHOLD) {
            edge = "RIGHT";
            spawnX = 50; // Spawn ở bên trái của room mới
            spawnY = playerY;
        }
        // TOP edge
        else if (playerY <= TRANSITION_THRESHOLD) {
            edge = "TOP";
            spawnX = playerX;
            spawnY = screenHeight - 100; // Spawn ở dưới của room mới
        }
        // BOTTOM edge
        else if (playerY >= screenHeight - TRANSITION_THRESHOLD) {
            edge = "BOTTOM";
            spawnX = playerX;
            spawnY = 50; // Spawn ở trên của room mới
        }

        // Nếu không ở mép nào
        if (edge == null) return null;

        // Tìm connection tương ứng
        for (RoomConnection conn : currentRoom.getConnections()) {
            if (conn.getDirection().equalsIgnoreCase(edge)) {
                // Tìm target room
                Room targetRoom = worldMap.getRooms().get(conn.getTargetRoomId());
                if (targetRoom != null) {
                    return new RoomTransition(
                            targetRoom.getId(),
                            spawnX,
                            spawnY
                    );
                }
            }
        }

        return null;
    }

    /**
     * Kiểm tra và hiển thị indicator khi player gần mép có connection
     */
    public boolean isNearTransition(Player player) {
        double playerX = player.getX();
        double playerY = player.getY();
        double threshold = TRANSITION_THRESHOLD * 2;

        return playerX <= threshold ||
                playerX >= screenWidth - threshold ||
                playerY <= threshold ||
                playerY >= screenHeight - threshold;
    }
}