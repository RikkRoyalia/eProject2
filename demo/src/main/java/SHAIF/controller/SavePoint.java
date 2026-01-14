package SHAIF.controller;

import SHAIF.model.Room;
import SHAIF.model.WorldMap;

/**
 * SavePoint - Điểm save game trong thế giới
 */
public class SavePoint {
    private String roomId;
    private double x, y;
    private boolean activated;

    public SavePoint(String roomId, double x, double y) {
        this.roomId = roomId;
        this.x = x;
        this.y = y;
        this.activated = false;
    }

    /**
     * Player tương tác với save point
     */
    public void interact() {
        activated = true;

        // Tạo save data
        SaveData data = new SaveData();
        data.setCurrentRoomId(roomId);
        data.setPlayerX(x);
        data.setPlayerY(y);
        data.setLastSavePointRoom(roomId);
        data.setLastSavePointX(x);
        data.setLastSavePointY(y);

        // Copy progress
        WorldMap worldMap = WorldMap.getInstance();
        for (Room room : worldMap.getRooms().values()) {
            if (room.isDiscovered()) {
                data.addDiscoveredRoom(room.getId());
            }
        }

        AbilityManager abilityMgr = AbilityManager.getInstance();
        for (String ability : abilityMgr.getUnlockedAbilities()) {
            data.addUnlockedAbility(ability);
        }

        data.setCompletionPercentage(abilityMgr.getCompletionPercentage());

        // Save
        MetroidvaniaSaveSystem.saveGame(data);

        System.out.println("GAME SAVED at Save Point in " + roomId);
    }

    // Getters
    public String getRoomId() { return roomId; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isActivated() { return activated; }
}