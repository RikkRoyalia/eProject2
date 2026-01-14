package SHAIF.model;

import java.util.ArrayList;
import java.util.List; /**
 * Room - Một khu vực trong world map
 */
public class Room {
    private String id;
    private String name;
    private double worldX, worldY; // Vị trí trong world
    private double width, height;
    private int mapId; // Map ID trong database
    private List<RoomConnection> connections;
    private String requiredAbility; // Ability cần để vào room này
    private boolean discovered; // Đã khám phá chưa

    public Room(String id, String name, double worldX, double worldY,
                double width, double height) {
        this.id = id;
        this.name = name;
        this.worldX = worldX;
        this.worldY = worldY;
        this.width = width;
        this.height = height;
        this.connections = new ArrayList<>();
        this.discovered = false;
    }

    public void addConnection(String direction, Room targetRoom,
                              double connX, double connY) {
        connections.add(new RoomConnection(
                direction, targetRoom.getId(), connX, connY,
                targetRoom.getWorldX(), targetRoom.getWorldY()
        ));
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getWorldX() { return worldX; }
    public double getWorldY() { return worldY; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getMapId() { return mapId; }
    public void setMapId(int mapId) { this.mapId = mapId; }
    public List<RoomConnection> getConnections() { return connections; }
    public String getRequiredAbility() { return requiredAbility; }
    public void setRequiredAbility(String ability) { this.requiredAbility = ability; }
    public boolean isDiscovered() { return discovered; }
    public void setDiscovered(boolean discovered) { this.discovered = discovered; }
}
