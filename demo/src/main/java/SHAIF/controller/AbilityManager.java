package SHAIF.controller;

import java.util.*;

/**
 * AbilityManager - Quản lý abilities/powers của player
 * Core feature của Metroidvania để mở khóa khu vực mới
 */
public class AbilityManager {
    private static AbilityManager instance;
    private Set<String> unlockedAbilities;
    private Map<String, Ability> allAbilities;

    private AbilityManager() {
        unlockedAbilities = new HashSet<>();
        allAbilities = new HashMap<>();
        initializeAbilities();
    }

    public static AbilityManager getInstance() {
        if (instance == null) {
            instance = new AbilityManager();
        }
        return instance;
    }

    private void initializeAbilities() {
        // Core Movement Abilities
        allAbilities.put("double_jump", new Ability(
                "double_jump",
                "Double Jump",
                "Jump twice in mid-air",
                "Allows access to high areas"
        ));

        allAbilities.put("wall_jump", new Ability(
                "wall_jump",
                "Wall Jump",
                "Jump off walls",
                "Climb vertical shafts"
        ));

        allAbilities.put("dash", new Ability(
                "dash",
                "Air Dash",
                "Dash through the air",
                "Cross large gaps"
        ));

        allAbilities.put("ground_pound", new Ability(
                "ground_pound",
                "Ground Pound",
                "Slam down from the air",
                "Break weak floors and hit switches"
        ));

        // Special Abilities
        allAbilities.put("swim", new Ability(
                "swim",
                "Swim",
                "Move through water",
                "Access underwater areas"
        ));

        allAbilities.put("morph_ball", new Ability(
                "morph_ball",
                "Morph Ball",
                "Roll into a small ball",
                "Access tight passages"
        ));

        allAbilities.put("grapple", new Ability(
                "grapple",
                "Grapple Hook",
                "Swing from grapple points",
                "Reach distant platforms"
        ));

        // Combat Abilities
        allAbilities.put("charge_dash", new Ability(
                "charge_dash",
                "Charge Dash",
                "Dash that destroys enemies",
                "Break through certain walls"
        ));

        allAbilities.put("energy_shield", new Ability(
                "energy_shield",
                "Energy Shield",
                "Temporary invincibility",
                "Pass through hazards"
        ));

        // Exploration
        allAbilities.put("x_ray_vision", new Ability(
                "x_ray_vision",
                "X-Ray Vision",
                "See hidden passages",
                "Reveal secret areas"
        ));
    }


    public void unlockAbility(String abilityId) {
        if (allAbilities.containsKey(abilityId)) {
            unlockedAbilities.add(abilityId);
            System.out.println("NEW ABILITY UNLOCKED: " +
                    allAbilities.get(abilityId).getName());
        }
    }

    /**
     * Kiểm tra có ability không
     */
    public boolean hasAbility(String abilityId) {
        return unlockedAbilities.contains(abilityId);
    }

    /**
     * Lấy danh sách abilities đã unlock
     */
    public List<String> getUnlockedAbilities() {
        return new ArrayList<>(unlockedAbilities);
    }

    /**
     * Kiểm tra có thể truy cập khu vực không
     */
    public boolean canAccessArea(String requiredAbility) {
        if (requiredAbility == null || requiredAbility.isEmpty()) {
            return true;
        }
        return hasAbility(requiredAbility);
    }

    /**
     * Lấy % hoàn thành game
     */
    public int getCompletionPercentage() {
        if (allAbilities.isEmpty()) return 0;
        return (unlockedAbilities.size() * 100) / allAbilities.size();
    }

    // Getters
    public Map<String, Ability> getAllAbilities() {
        return new HashMap<>(allAbilities);
    }

    /**
     * Reset cho new game
     */
    public void reset() {
        unlockedAbilities.clear();
    }

    /**
     * Save/Load
     */
    public String serialize() {
        return String.join(",", unlockedAbilities);
    }

    public void deserialize(String data) {
        unlockedAbilities.clear();
        if (data != null && !data.isEmpty()) {
            String[] abilities = data.split(",");
            unlockedAbilities.addAll(Arrays.asList(abilities));
        }
    }
}

