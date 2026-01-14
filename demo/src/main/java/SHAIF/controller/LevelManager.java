package SHAIF.controller;

import java.util.HashSet;
import java.util.Set;

public class LevelManager {
    private static LevelManager instance;
    private int currentLevel = 1;
    private final Set<Integer> unlockedLevels;
    private final int maxLevels = 5;

    private LevelManager() {
        unlockedLevels = new HashSet<>();
        unlockedLevels.add(1); // Level 1 unlocked by default
    }

    public static LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        if (level >= 1 && level <= maxLevels && isUnlocked(level)) {
            this.currentLevel = level;
        }
    }

    public void nextLevel() {
        if (currentLevel < maxLevels) {
            currentLevel++;
        }
    }

    public void unlockLevel(int level) {
        if (level >= 1 && level <= maxLevels) {
            unlockedLevels.add(level);
        }
    }

    public boolean isUnlocked(int level) {
        return unlockedLevels.contains(level);
    }

    public int getHighestUnlockedLevel() {
        return unlockedLevels.stream().mapToInt(Integer::intValue).max().orElse(1);
    }

    public int getMaxLevels() {
        return maxLevels;
    }

    public void checkUnlocks(int totalScore) {
        // Unlock levels dựa trên total score
        if (totalScore >= 5000 && !isUnlocked(2)) {
            unlockLevel(2);
        }
        if (totalScore >= 10000 && !isUnlocked(3)) {
            unlockLevel(3);
        }
        if (totalScore >= 20000 && !isUnlocked(4)) {
            unlockLevel(4);
        }
        if (totalScore >= 50000 && !isUnlocked(5)) {
            unlockLevel(5);
        }
    }
}

