package SHAIF.model;

/**
 * RoomTransition - Data cho việc chuyển room
 */
public class RoomTransition {
    private String targetRoomId;
    private double spawnX;
    private double spawnY;

    public RoomTransition(String targetRoomId, double spawnX, double spawnY) {
        this.targetRoomId = targetRoomId;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public String getTargetRoomId() { return targetRoomId; }
    public double getSpawnX() { return spawnX; }
    public double getSpawnY() { return spawnY; }
}
