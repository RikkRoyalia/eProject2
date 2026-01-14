package SHAIF.controller;

import java.util.HashMap;
import java.util.Map;

public class UpgradeShop {
    private static UpgradeShop instance;
    private int coins = 0;
    private final Map<String, Upgrade> upgrades;

    private UpgradeShop() {
        upgrades = new HashMap<>();
        initializeUpgrades();
    }

    public static UpgradeShop getInstance() {
        if (instance == null) {
            instance = new UpgradeShop();
        }
        return instance;
    }

    private void initializeUpgrades() {
        upgrades.put("max_health", new Upgrade("Max Health", 100, 0, 5));
        upgrades.put("walk_speed", new Upgrade("Walk Speed", 150, 0, 3));
        upgrades.put("jump_height", new Upgrade("Jump Height", 200, 0, 3));
        upgrades.put("dash_distance", new Upgrade("Dash Distance", 120, 0, 3));
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void setCoins(int amount) {
        coins = amount;
    }

    public int getCoins() {
        return coins;
    }

    public boolean buyUpgrade(String upgradeId) {
        Upgrade upgrade = upgrades.get(upgradeId);
        if (upgrade != null && coins >= upgrade.getCost() && upgrade.getLevel() < upgrade.getMaxLevel()) {
            coins -= upgrade.getCost();
            upgrade.increaseLevel();
            return true;
        }
        return false;
    }

    public Upgrade getUpgrade(String upgradeId) {
        return upgrades.get(upgradeId);
    }

    public Map<String, Upgrade> getAllUpgrades() {
        return new HashMap<>(upgrades);
    }

    public static class Upgrade {
        private final String name;
        private final int baseCost;
        private int level;
        private final int maxLevel;

        public Upgrade(String name, int baseCost, int level, int maxLevel) {
            this.name = name;
            this.baseCost = baseCost;
            this.level = level;
            this.maxLevel = maxLevel;
        }

        public String getName() {
            return name;
        }

        public int getCost() {
            return baseCost * (level + 1);
        }

        public int getLevel() {
            return level;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public void increaseLevel() {
            if (level < maxLevel) {
                level++;
            }
        }

        public double getMultiplier() {
            return 1.0 + (level * 0.2); // 20% increase per level
        }
    }
}

