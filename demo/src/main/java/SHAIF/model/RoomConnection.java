package SHAIF.model;

/**
 * RoomConnection - Kết nối giữa các rooms
 * Stores connection info including spawn positions from database
 */
public class RoomConnection {
    private String direction; // "LEFT", "RIGHT", "TOP", "BOTTOM"
    private String targetRoomId;
    private double spawnX, spawnY; // Spawn position in target room (from room_connections.spawn_x, spawn_y)
    private double targetWorldX, targetWorldY; // World position của target room
    private String requiredAbility; // Ability needed to use this connection

    // Main constructor used by DAO
    public RoomConnection(String direction, String targetRoomId,
                          double spawnX, double spawnY,
                          double targetWorldX, double targetWorldY) {
        this.direction = direction;
        this.targetRoomId = targetRoomId;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.targetWorldX = targetWorldX;
        this.targetWorldY = targetWorldY;
    }

    // Overloaded constructor with required ability
    public RoomConnection(String direction, String targetRoomId,
                          double spawnX, double spawnY,
                          double targetWorldX, double targetWorldY,
                          String requiredAbility) {
        this(direction, targetRoomId, spawnX, spawnY, targetWorldX, targetWorldY);
        this.requiredAbility = requiredAbility;
    }

    // Getters
    public String getDirection() { return direction; }
    public String getTargetRoomId() { return targetRoomId; }

    // ⭐ CRITICAL: These return the spawn coordinates from database
    public double getSpawnX() { return spawnX; }
    public double getSpawnY() { return spawnY; }

    // Legacy names for compatibility
    public double getConnectionX() { return spawnX; }
    public double getConnectionY() { return spawnY; }

    // World coordinates
    public double getTargetX() { return targetWorldX; }
    public double getTargetY() { return targetWorldY; }

    public String getRequiredAbility() { return requiredAbility; }

    @Override
    public String toString() {
        return String.format("RoomConnection[%s -> %s at (%.0f, %.0f)]",
                direction, targetRoomId, spawnX, spawnY);
    }
}