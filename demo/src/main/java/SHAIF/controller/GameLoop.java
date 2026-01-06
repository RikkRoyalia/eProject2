package SHAIF.controller;

import SHAIF.model.*;
import SHAIF.screen.MenuScreen;
import SHAIF.view.GameHUD;
import SHAIF.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Iterator;

public class GameLoop {

    AnimationTimer timer;

    private final GameView gameView;
    protected final Player player;
    final Enemy enemy;
    final Bullet bullet;
    final DashController dashController;
    private final Stage primaryStage;
    private final MenuScreen menuScreen;
    final GameStats stats;
    final GameHUD hud;
    private final ComboSystem comboSystem;
    final AchievementManager achievementManager;
    final GameData gameData;
    private boolean gameWon = false;
    boolean isPaused = false;
    private boolean perfectRun = true; // Track for perfect run achievement

    public GameLoop(GameView gameView, Player player, Enemy enemy, Bullet bullet,
                    DashController dashController, Stage primaryStage, MenuScreen menuScreen,
                    GameStats stats, GameHUD hud, ComboSystem comboSystem) {
        this.gameView = gameView;
        this.player = player;
        this.enemy = enemy;
        this.bullet = bullet;
        this.dashController = dashController;
        this.primaryStage = primaryStage;
        this.menuScreen = menuScreen;
        this.stats = stats;
        this.hud = hud;
        this.comboSystem = comboSystem;
        this.achievementManager = AchievementManager.getInstance();
        this.gameData = GameData.getInstance();
    }

    public void start() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isPaused) return;

                // Cập nhật game
                player.applyGravityWithPlatforms(gameView.getPlatforms(), gameView.getGroundLevel());
                player.update();
                dashController.update();

                if (enemy.shouldShoot(now)) {
                    bullet.shoot(enemy.getX(), enemy.getY() + 20);
                }

                enemy.update();
                bullet.update();

                // Update items
                for (Item item : gameView.getItems()) {
                    item.update();
                }

                // Update HUD
                hud.update();

                // Check achievements
                checkAchievements();

                checkCollisions();
            }
        };
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    private void gameOver() {
        // Dừng vòng lặp game
        stop();

        // Update game data
        gameData.setHighScore(stats.getScore());
        gameData.addTotalScore(stats.getScore());
        gameData.addPlayTime((int) stats.getPlayTime());
        gameData.incrementDeaths();
        gameData.save();

        // Chạy UI thread
        javafx.application.Platform.runLater(() -> {
            // Hiển thị thông báo
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION
            );
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over!");
            alert.setContentText(
                    "Final Score: " + stats.getScore() + "\n" +
                            "Time: " + stats.getFormattedTime() + "\n" +
                            "Enemies Killed: " + stats.getEnemiesKilled() + "\n" +
                            "Coins Collected: " + stats.getCoinsCollected()
            );
            alert.showAndWait();

            // Quay về menu
            menuScreen.show();
            primaryStage.setScene(menuScreen.getScene());
        });
    }

    private void victory() {
        // Dừng vòng lặp game
        stop();

        // Save achievements và game data
        int currentLevel = LevelManager.getInstance().getCurrentLevel();
        achievementManager.checkAchievement("first_steps", currentLevel >= 1 ? 1 : 0);

        if (perfectRun) {
            achievementManager.checkAchievement("perfect_run", 1);
        }

        // Check level master
        if (currentLevel >= 5) {
            achievementManager.checkAchievement("level_master", 1);
        }

        // Update game data
        gameData.setHighScore(stats.getScore());
        gameData.addTotalScore(stats.getScore());
        gameData.addPlayTime((int) stats.getPlayTime());

        // Unlock level tiếp theo khi hoàn thành level hiện tại
        LevelManager levelManager = LevelManager.getInstance();
        if (currentLevel < 5) {
            levelManager.unlockLevel(currentLevel + 1);
        }

        // Check unlocks dựa trên total score
        levelManager.checkUnlocks(gameData.getTotalScore());
        gameData.setLevelsUnlocked(levelManager.getHighestUnlockedLevel());
        gameData.save();

        // Chạy UI thread
        javafx.application.Platform.runLater(() -> {
            // Hiển thị thông báo
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION
            );
            alert.setTitle("Victory!");
            alert.setHeaderText("Level Complete!");
            alert.setContentText(
                    "Congratulations! You completed the level!\n\n" +
                            "Final Score: " + stats.getScore() + "\n" +
                            "Time: " + stats.getFormattedTime() + "\n" +
                            "Enemies Killed: " + stats.getEnemiesKilled() + "\n" +
                            "Coins Collected: " + stats.getCoinsCollected() +
                            (perfectRun ? "\n\nPerfect Run!" : "")
            );
            alert.showAndWait();

            // Next level hoặc quay về menu
            LevelManager.getInstance().nextLevel();
            menuScreen.show();
            primaryStage.setScene(menuScreen.getScene());
        });
    }

    protected void checkCollisions() {
        // Player dash vào enemy (dùng interface method)
        if (dashController.checkCollision(enemy) && enemy.isActive()) {
            enemy.defeat();
            stats.killEnemy();
            gameData.incrementEnemiesKilled();

            if (enemy.isBoss()) {
                achievementManager.checkAchievement("boss_slayer", 1);
            } else {
                achievementManager.progressAchievement("dash_king", 1);
            }

            dashController.stopDash();
        }

        // Bullet trúng player (dùng interface method)
        if (bullet.intersects(player.getCurrentShape()) && bullet.isActive()) {
            if (player.getCurrentForm() == FormType.SQUARE) {
                // Square chặn đạn
                bullet.deactivate();
            } else {
                // Nhận damage
                player.takeDamage();
                bullet.deactivate(); // Deactivate sau khi damage
            }
        }

        // Chạm đích
        if (player.getCurrentShape().getBoundsInParent()
                .intersects(gameView.getGoal().getBoundsInParent())) {
            gameWon = true;
            victory();
            return;
        }

        // Rơi vào pit
        for (Rectangle pit : gameView.getPits()) {
            double px = player.getX();
            double py = player.getY();
            double ph = player.getHeight();

            if (px >= pit.getX() && px <= pit.getX() + pit.getWidth() &&
                    py + ph >= pit.getY()) {
                gameOver();
                return;
            }
        }

        // Player thu thập items
        Iterator<Item> itemIterator = gameView.getItems().iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (item.isActive() && item.intersects(player.getCurrentShape())) {
                handleItemCollection(item);
                item.collect();
            }
        }

        // Bị trúng đạn quá số lần
        if (player.isDead()) {
            gameOver();
            return;
        }
    }

    private void handleItemCollection(Item item) {
        stats.collectItem(item.getItemType());
        gameData.incrementItemsCollected();
        achievementManager.progressAchievement("collector", 1);

        switch (item.getItemType()) {
            case HEALTH:
                player.heal();
                break;
            case DASH_BOOST:
                player.activateDashBoost(15); // 15 giây
                break;
            case SHIELD:
                player.activateShield(10); // 10 giây
                break;
            case SPEED_BOOST:
                player.activateSpeedBoost(10); // 10 giây
                break;
            case DOUBLE_JUMP:
                player.activateDoubleJump(); // Permanent
                break;
            case COIN:
                UpgradeShop.getInstance().addCoins(10);
                gameData.addCoins(10);
                achievementManager.progressAchievement("coin_collector", 1);
                break;
        }
    }

    void checkAchievements() {
        // Check perfect run
        if (player.getHitCount() > 0) {
            perfectRun = false;
        }

        // Check speed demon (under 60 seconds)
        if (stats.getPlayTime() < 60 && gameWon) {
            achievementManager.checkAchievement("speed_demon", 1);
        }

        // Check survivor (5 minutes = 300 seconds)
        if (stats.getPlayTime() >= 300) {
            achievementManager.checkAchievement("survivor", 1);
        }
    }
}
