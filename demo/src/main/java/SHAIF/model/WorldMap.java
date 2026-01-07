package SHAIF.model;

import SHAIF.database.MetroidvaniaDAO;
import SHAIF.database.RoomData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WorldMap - Quản lý toàn bộ thế giới game kết nối liền mạch
 */
public class WorldMap {
    private static WorldMap instance;

    private Map<String, Room> rooms;
    private String currentRoomId;
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
        System.out.println("\n=== Initializing World Map ===");

        // Load all rooms từ database
        List<RoomData> roomDataList = MetroidvaniaDAO.loadAllRooms();

        if (roomDataList.isEmpty()) {
            System.err.println("⚠️  No rooms found in database! Creating default starting area.");
            createDefaultStartingArea();
            return;
        }

        // Create Room objects từ database
        for (RoomData data : roomDataList) {
            Room room = new Room(
                    data.getRoomId(),
                    data.getRoomName(),
                    data.getWorldX(),
                    data.getWorldY(),
                    data.getScreenWidth(),
                    data.getScreenHeight()
            );
            room.setMapId(data.getMapId());
            room.setRequiredAbility(data.getRequiredAbility());

            rooms.put(room.getId(), room);
            System.out.println("  Room loaded: " + room.getName() + " (ID: " + room.getId() + ")");
        }

        // Load connections cho mỗi room
        for (Room room : rooms.values()) {
            List<RoomConnection> connections = MetroidvaniaDAO.loadRoomConnections(room.getMapId());
            for (RoomConnection conn : connections) {
                room.getConnections().add(conn);
                System.out.println("    Connection: " + conn.getDirection() + " -> " + conn.getTargetRoomId());
            }
        }

        // Set starting position
        if (rooms.containsKey("starting_area")) {
            currentRoomId = "starting_area";
            playerWorldX = 100;
            playerWorldY = 680;
            System.out.println("\n✓ World initialized at: " + currentRoomId);
        } else {
            // Fallback: use first room
            currentRoomId = rooms.keySet().iterator().next();
            playerWorldX = 100;
            playerWorldY = 600;
            System.err.println("⚠️  'starting_area' not found, using: " + currentRoomId);
        }

        System.out.println("Total rooms: " + rooms.size());
        System.out.println("================================\n");
    }

    private void createDefaultStartingArea() {
        Room startingArea = new Room("starting_area", "Starting Area",
                0, 0, 1280, 720);
        startingArea.setMapId(1);

        rooms.put(startingArea.getId(), startingArea);
        currentRoomId = "starting_area";
        playerWorldX = 100;
        playerWorldY = 680;

        System.out.println("✓ Default starting area created");
    }

    public Room getCurrentRoom() {
        return rooms.get(currentRoomId);
    }

    /**
     * Kiểm tra nếu player đi qua cửa để chuyển room
     * (Legacy method - now handled by RoomTransitionDetector)
     */
    public RoomTransition checkRoomTransition(double playerX, double playerY) {
        // This is now handled by RoomTransitionDetector
        return null;
    }

    public void transitionToRoom(String roomId, double x, double y) {
        if (rooms.containsKey(roomId)) {
            currentRoomId = roomId;
            playerWorldX = x;
            playerWorldY = y;

            System.out.println("Transitioned to: " + getCurrentRoom().getName());
            System.out.println("Position: (" + x + ", " + y + ")");
        } else {
            System.err.println("❌ Room not found: " + roomId);
        }
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