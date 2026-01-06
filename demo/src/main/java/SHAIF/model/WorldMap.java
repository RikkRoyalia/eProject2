package SHAIF.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WorldMap - Quản lý toàn bộ thế giới game kết nối liền mạch
 * Thay thế hệ thống level riêng biệt
 */
public class WorldMap {
    private static WorldMap instance;

    // Danh sách các rooms (thay vì levels riêng biệt)
    private Map<String, Room> rooms;

    // Room hiện tại player đang ở
    private String currentRoomId;

    // Vị trí player trong world (tọa độ tuyệt đối)
    private double playerWorldX;
    private double playerWorldY;

    private WorldMap() {
        rooms = new HashMap<>();
        initializeWorld();
    }

    public static WorldMap getInstance() {
        if (instance == null) {
            instance = new WorldMap();
        }
        return instance;
    }

    private void initializeWorld() {
        // Tạo các rooms kết nối với nhau
        // Ví dụ: Starting Area -> Forest -> Cave -> Boss Room

        Room startingArea = new Room("starting_area", "Starting Area",
                0, 0, 2000, 1200);
        startingArea.setMapId(1); // Map ID trong database

        Room forest = new Room("forest", "Dark Forest",
                2000, 0, 2500, 1200);
        forest.setMapId(2);

        Room cave = new Room("cave", "Underground Cave",
                2000, 1200, 2000, 1500);
        cave.setMapId(3);
        cave.setRequiredAbility("double_jump"); // Cần double jump để vào

        Room bossRoom = new Room("boss_room", "Boss Chamber",
                4500, 0, 1500, 1200);
        bossRoom.setMapId(4);
        bossRoom.setRequiredAbility("dash_through_walls");

        // Thêm connections giữa các rooms
        startingArea.addConnection("right", forest, 1980, 600);
        forest.addConnection("left", startingArea, 20, 600);
        forest.addConnection("down", cave, 200, 1180);
        forest.addConnection("right", bossRoom, 2480, 600);
        cave.addConnection("up", forest, 200, 20);
        bossRoom.addConnection("left", forest, 20, 600);

        rooms.put(startingArea.getId(), startingArea);
        rooms.put(forest.getId(), forest);
        rooms.put(cave.getId(), cave);
        rooms.put(bossRoom.getId(), bossRoom);

        currentRoomId = "starting_area";
        playerWorldX = 100;
        playerWorldY = 680;
    }

    public Room getCurrentRoom() {
        return rooms.get(currentRoomId);
    }

    public boolean canEnterRoom(String roomId, List<String> playerAbilities) {
        Room room = rooms.get(roomId);
        if (room == null) return false;

        String requiredAbility = room.getRequiredAbility();
        if (requiredAbility == null) return true;

        return playerAbilities.contains(requiredAbility);
    }

    /**
     * Kiểm tra nếu player đi qua cửa để chuyển room
     */
    public RoomTransition checkRoomTransition(double playerX, double playerY) {
        Room current = getCurrentRoom();

        for (RoomConnection conn : current.getConnections()) {
            if (isNearConnection(playerX, playerY, conn)) {
                return new RoomTransition(
                        conn.getTargetRoomId(),
                        conn.getTargetX(),
                        conn.getTargetY()
                );
            }
        }

        return null;
    }

    private boolean isNearConnection(double x, double y, RoomConnection conn) {
        double distance = Math.sqrt(
                Math.pow(x - conn.getConnectionX(), 2) +
                        Math.pow(y - conn.getConnectionY(), 2)
        );
        return distance < 50; // Trong vòng 50 pixels
    }

    public void transitionToRoom(String roomId, double x, double y) {
        currentRoomId = roomId;
        playerWorldX = x;
        playerWorldY = y;
    }

    // Getters
    public Map<String, Room> getRooms() { return rooms; }
    public String getCurrentRoomId() { return currentRoomId; }
    public double getPlayerWorldX() { return playerWorldX; }
    public double getPlayerWorldY() { return playerWorldY; }

    public void setPlayerWorldPosition(double x, double y) {
        this.playerWorldX = x;
        this.playerWorldY = y;
    }
}

