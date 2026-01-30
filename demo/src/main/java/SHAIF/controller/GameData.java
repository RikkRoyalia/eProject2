package SHAIF.controller;

import java.io.*;
import java.util.Properties;

public class GameData {
    private static GameData instance;
    private final Properties props;
    private final String dataFile = "gamedata.properties";

    private GameData() {
        props = new Properties();
        load();
    }

    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }

    public void load() {
        try (FileInputStream fis = new FileInputStream(dataFile)) {
            props.load(fis);
        } catch (IOException e) {
            // File không tồn tại, sử dụng defaults
            initializeDefaults();
        }
    }

    public void save() {
        try (FileOutputStream fos = new FileOutputStream(dataFile)) {
            props.store(fos, "Game Data");
        } catch (IOException e) {
            System.err.println("Error saving game data: " + e.getMessage());
        }
    }

    private void initializeDefaults() {
        props.setProperty("highScore", "0");
        props.setProperty("totalScore", "0");
        props.setProperty("totalPlayTime", "0");
        props.setProperty("deaths", "0");
        props.setProperty("enemiesKilled", "0");
        props.setProperty("itemsCollected", "0");
        props.setProperty("coins", "0");
        props.setProperty("levelsUnlocked", "1");
    }

    // Getters and Setters
    public int getHighScore() {
        return Integer.parseInt(props.getProperty("highScore", "0"));
    }

    public void setHighScore(int score) {
        int current = getHighScore();
        if (score > current) {
            props.setProperty("highScore", String.valueOf(score));
        }
    }

    public int getTotalScore() {
        return Integer.parseInt(props.getProperty("totalScore", "0"));
    }

    public void addTotalScore(int score) {
        int current = getTotalScore();
        props.setProperty("totalScore", String.valueOf(current + score));
    }

    public int getTotalPlayTime() {
        return Integer.parseInt(props.getProperty("totalPlayTime", "0"));
    }

    public void addPlayTime(int seconds) {
        int current = getTotalPlayTime();
        props.setProperty("totalPlayTime", String.valueOf(current + seconds));
    }

    public int getDeaths() {
        return Integer.parseInt(props.getProperty("deaths", "0"));
    }

    public void incrementDeaths() {
        int current = getDeaths();
        props.setProperty("deaths", String.valueOf(current + 1));
    }

    public int getEnemiesKilled() {
        return Integer.parseInt(props.getProperty("enemiesKilled", "0"));
    }

    public void incrementEnemiesKilled() {
        int current = getEnemiesKilled();
        props.setProperty("enemiesKilled", String.valueOf(current + 1));
    }

    public int getItemsCollected() {
        return Integer.parseInt(props.getProperty("itemsCollected", "0"));
    }

    public void incrementItemsCollected() {
        int current = getItemsCollected();
        props.setProperty("itemsCollected", String.valueOf(current + 1));
    }

    public int getCoins() {
        return Integer.parseInt(props.getProperty("coins", "0"));
    }

    public void addCoins(int coins) {
        int current = getCoins();
        props.setProperty("coins", String.valueOf(current + coins));
    }
}

