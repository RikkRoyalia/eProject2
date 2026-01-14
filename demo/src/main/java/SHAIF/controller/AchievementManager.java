package SHAIF.controller;

import java.util.HashMap;
import java.util.Map;

public class AchievementManager {
    private static AchievementManager instance;
    private final Map<String, Integer> achievements;
    private final Map<String, Integer> achievementProgress;

    private AchievementManager() {
        achievements = new HashMap<>();
        achievementProgress = new HashMap<>();
        initializeAchievements();
    }

    public static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    private void initializeAchievements() {
        // Initialize achievement progress
        achievementProgress.put("first_steps", 0);
        achievementProgress.put("shape_master", 0);
        achievementProgress.put("dash_king", 0);
        achievementProgress.put("perfect_run", 0);
        achievementProgress.put("speed_demon", 0);
        achievementProgress.put("collector", 0);
        achievementProgress.put("survivor", 0);
        achievementProgress.put("coin_collector", 0);
        achievementProgress.put("level_master", 0);
        achievementProgress.put("boss_slayer", 0);
    }

    public void checkAchievement(String achievementId, int value) {
        if (!achievements.containsKey(achievementId) && value > 0) {
            achievements.put(achievementId, 1);
            System.out.println("Achievement unlocked: " + achievementId);
        }
    }

    public void progressAchievement(String achievementId, int progress) {
        int current = achievementProgress.getOrDefault(achievementId, 0);
        achievementProgress.put(achievementId, current + progress);
        
        // Check if achievement should be unlocked
        int required = getRequiredProgress(achievementId);
        if (achievementProgress.get(achievementId) >= required && !achievements.containsKey(achievementId)) {
            achievements.put(achievementId, 1);
            System.out.println("Achievement unlocked: " + achievementId);
        }
    }

    private int getRequiredProgress(String achievementId) {
        switch (achievementId) {
            case "shape_master": return 100;
            case "dash_king": return 10;
            case "collector": return 20;
            case "coin_collector": return 50;
            default: return 1;
        }
    }

    public boolean isUnlocked(String achievementId) {
        return achievements.containsKey(achievementId);
    }

    public int getProgress(String achievementId) {
        return achievementProgress.getOrDefault(achievementId, 0);
    }

    public Map<String, Integer> getAllAchievements() {
        return new HashMap<>(achievements);
    }
}

