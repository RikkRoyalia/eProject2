package SHAIF.database;

/**
 * RoomData - Data class for room info
 */
public class RoomData {
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
