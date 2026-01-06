package SHAIF.controller;

import java.io.*;
import java.util.*;

/**
 * SaveData - Dữ liệu cần save
 */
public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    // Player state
    private double playerX;
    private double playerY;
    private int health;
    private String currentRoomId;

    // Progress
    private Set<String> discoveredRooms;
    private Set<String> unlockedAbilities;
    private Set<String> defeatedBosses;
    private Set<String> collectedItems;
    private Map<String, Boolean> permanentChanges; // Switches, broken walls, etc.

    // Stats
    private int totalPlayTime;
    private int deathCount;
    private double completionPercentage;

    // Save point location
    private String lastSavePointRoom;
    private double lastSavePointX;
    private double lastSavePointY;

    public SaveData() {
        discoveredRooms = new HashSet<>();
        unlockedAbilities = new HashSet<>();
        defeatedBosses = new HashSet<>();
        collectedItems = new HashSet<>();
        permanentChanges = new HashMap<>();
    }

    // Getters and Setters
    public double getPlayerX() { return playerX; }
    public void setPlayerX(double x) { this.playerX = x; }

    public double getPlayerY() { return playerY; }
    public void setPlayerY(double y) { this.playerY = y; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public String getCurrentRoomId() { return currentRoomId; }
    public void setCurrentRoomId(String id) { this.currentRoomId = id; }

    public Set<String> getDiscoveredRooms() { return discoveredRooms; }
    public void addDiscoveredRoom(String roomId) { discoveredRooms.add(roomId); }

    public Set<String> getUnlockedAbilities() { return unlockedAbilities; }
    public void addUnlockedAbility(String ability) { unlockedAbilities.add(ability); }

    public Set<String> getDefeatedBosses() { return defeatedBosses; }
    public void addDefeatedBoss(String bossId) { defeatedBosses.add(bossId); }

    public Set<String> getCollectedItems() { return collectedItems; }
    public void addCollectedItem(String itemId) { collectedItems.add(itemId); }

    public Map<String, Boolean> getPermanentChanges() { return permanentChanges; }
    public void setPermanentChange(String changeId, boolean value) {
        permanentChanges.put(changeId, value);
    }

    public int getTotalPlayTime() { return totalPlayTime; }
    public void setTotalPlayTime(int time) { this.totalPlayTime = time; }

    public int getDeathCount() { return deathCount; }
    public void incrementDeathCount() { this.deathCount++; }

    public double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(double percentage) {
        this.completionPercentage = percentage;
    }

    public String getLastSavePointRoom() { return lastSavePointRoom; }
    public void setLastSavePointRoom(String room) { this.lastSavePointRoom = room; }

    public double getLastSavePointX() { return lastSavePointX; }
    public void setLastSavePointX(double x) { this.lastSavePointX = x; }

    public double getLastSavePointY() { return lastSavePointY; }
    public void setLastSavePointY(double y) { this.lastSavePointY = y; }
}