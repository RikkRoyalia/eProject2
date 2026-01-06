package SHAIF.model;

/**
 * RoomConnection - Kết nối giữa các rooms
 */
public class RoomConnection {
    private String direction; // "left", "right", "up", "down"
    private String targetRoomId;
    private double connectionX, connectionY; // Vị trí cửa trong room hiện tại
    private double targetWorldX, targetWorldY; // World position của target room

    public RoomConnection(String direction, String targetRoomId,
                          double connectionX, double connectionY,
                          double targetWorldX, double targetWorldY) {
        this.direction = direction;
        this.targetRoomId = targetRoomId;
        this.connectionX = connectionX;
        this.connectionY = connectionY;
        this.targetWorldX = targetWorldX;
        this.targetWorldY = targetWorldY;
    }

    // Getters
    public String getDirection() { return direction; }
    public String getTargetRoomId() { return targetRoomId; }
    public double getConnectionX() { return connectionX; }
    public double getConnectionY() { return connectionY; }
    public double getTargetX() { return targetWorldX; }
    public double getTargetY() { return targetWorldY; }
}
