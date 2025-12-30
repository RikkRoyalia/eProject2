package SHAIF.controller;

import SHAIF.model.ItemType;

public class GameStats {
    private int score = 0;
    private int enemiesKilled = 0;
    private int coinsCollected = 0;
    private long startTime;
    private long playTime = 0;
    private boolean isPaused = false;
    private long pauseStartTime = 0;
    private long totalPauseTime = 0;

    public GameStats() {
        this.startTime = System.nanoTime();
    }

    public void addScore(int points) {
        score += points;
    }

    public void killEnemy() {
        enemiesKilled++;
        addScore(100);
    }

    public void collectItem(ItemType itemType) {
        switch (itemType) {
            case COIN:
                coinsCollected++;
                addScore(10);
                break;
            case HEALTH:
                addScore(25);
                break;
            case DASH_BOOST:
            case SHIELD:
            case SPEED_BOOST:
            case DOUBLE_JUMP:
                addScore(50);
                break;
        }
    }

    public void pause() {
        if (!isPaused) {
            isPaused = true;
            pauseStartTime = System.nanoTime();
        }
    }

    public void resume() {
        if (isPaused) {
            isPaused = false;
            totalPauseTime += System.nanoTime() - pauseStartTime;
        }
    }

    public void update() {
        if (!isPaused) {
            long currentTime = System.nanoTime();
            playTime = (currentTime - startTime - totalPauseTime) / 1_000_000_000L; // Convert to seconds
        }
    }

    public int getScore() {
        return score;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }

    public long getPlayTime() {
        return playTime;
    }

    public String getFormattedTime() {
        long minutes = playTime / 60;
        long seconds = playTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

