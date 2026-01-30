package SHAIF.controller;

import SHAIF.model.*;
import SHAIF.screen.MenuScreen;
import SHAIF.view.GameHUD;
import SHAIF.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLoop {

    AnimationTimer timer;

    private final GameView gameView;
    protected final Player player;
    protected final List<Enemy> enemies;  // CHANGED: from single Enemy
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
    private boolean perfectRun = true;

    // CHANGED: Constructor now accepts List<Enemy>
    public GameLoop(GameView gameView, Player player, List<Enemy> enemies,
                    DashController dashController, Stage primaryStage, MenuScreen menuScreen,
                    GameStats stats, GameHUD hud, ComboSystem comboSystem) {
        this.gameView = gameView;
        this.player = player;
        this.enemies = enemies != null ? enemies : new ArrayList<>();
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

                // Update player
                player.applyGravityWithPlatforms(gameView.getPlatforms(), gameView.getGroundLevel());
                player.update();
                resolveHorizontalCollisions();
                dashController.update();

                // CHANGED: Update all enemies
                for (Enemy enemy : enemies) {
                    enemy.updateWithShooting(now);
                }

                // Update items
                for (Item item : gameView.getItems()) {
                    item.update();
                }

                hud.update();
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
        stop();

        gameData.setHighScore(stats.getScore());
        gameData.addTotalScore(stats.getScore());
        gameData.addPlayTime((int) stats.getPlayTime());
        gameData.incrementDeaths();
        gameData.save();

        javafx.application.Platform.runLater(() -> {
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

            menuScreen.show();
            primaryStage.setScene(menuScreen.getScene());
        });
    }

    private void checkHazardCollision() {
        javafx.geometry.Bounds playerBounds = player.getCurrentShape().getBoundsInParent();

        for (Rectangle hazard : gameView.getObstacles()) {
            double px = player.getX();
            double py = player.getY();
            double ph = player.getHeight();

            if (px >= hazard.getX() && px <= hazard.getX() + hazard.getWidth() &&
                    py + ph >= hazard.getY()) {
                gameOver();
                return;
            }
//            if (playerBounds.intersects(hazard.getBoundsInParent())) {
//                System.out.println("Player hit hazard!");
//                gameOver();
//                return;
//            }
        }
    }

    protected void checkCollisions() {
        // Check all enemies
        for (Enemy enemy : enemies) {
            // Player dash into enemy
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

            // Contact damage (touching enemy)
            if (!player.isDashing() && enemy.isActive() && !player.isInvincible()) {
                if (enemy.intersects(player.getCurrentShape())) {
                    player.takeDamage();
                    player.applyKnockback(enemy.getX(), enemy.getKnockbackForce());
                    System.out.println("Player hit by enemy contact!");
                }
            }

            // Check enemy's bullet
            Bullet bullet = enemy.getBullet();
            if (bullet != null && bullet.intersects(player.getCurrentShape()) && bullet.isActive()) {
                if (player.getCurrentForm() == FormType.SQUARE) {
                    bullet.deactivate();
                } else {
                    if (!player.isInvincible()) {  // Check invincibility
                        player.takeDamage();
                        player.applyKnockback(enemy.getX(), enemy.getKnockbackForce());
                        bullet.deactivate();
                    }
                }
            }
        }

        checkHazardCollision(); // Check ALL hazards!

        // Item collection
        Iterator<Item> itemIterator = gameView.getItems().iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (item.isActive() && item.intersects(player.getCurrentShape())) {
                handleItemCollection(item);
                item.collect();
            }
        }

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
                player.activateDashBoost(15);
                break;
            case SHIELD:
                player.activateShield(10);
                break;
            case SPEED_BOOST:
                player.activateSpeedBoost(10);
                break;
            case DOUBLE_JUMP:
                player.activateDoubleJump();
                break;
            case COIN:
                UpgradeShop.getInstance().addCoins(10);
                gameData.addCoins(10);
                achievementManager.progressAchievement("coin_collector", 1);
                break;
        }
    }

    void checkAchievements() {
        if (player.getHitCount() > 0) {
            perfectRun = false;
        }

        if (stats.getPlayTime() < 60 && gameWon) {
            achievementManager.checkAchievement("speed_demon", 1);
        }

        if (stats.getPlayTime() >= 300) {
            achievementManager.checkAchievement("survivor", 1);
        }
    }

    /**
     * IMPROVED: Horizontal collision with vertical position check
     */
    protected void resolveHorizontalCollisions() {
        if (player.isDashing()) return;

        Bounds playerBounds = player.getCurrentShape().getBoundsInParent();
        double playerX = player.getX();
        double playerY = player.getY();
        double playerBottom = playerY + player.getHeight();
        double halfWidth = player.getWidth() / 2.0;

        int velocityX = 0;
        if (player.isMovingLeft()) velocityX = -1;
        if (player.isMovingRight()) velocityX = 1;

        if (velocityX == 0) return;

        // Check obstacles
        for (Rectangle obstacle : gameView.getObstacles()) {
            if (obstacle.getStyleClass().contains("pit")) continue;

            Bounds obsBounds = obstacle.getBoundsInParent();
            if (!playerBounds.intersects(obsBounds)) continue;

            double obsLeft = obstacle.getX();
            double obsRight = obstacle.getX() + obstacle.getWidth();
            double obsTop = obstacle.getY();

            boolean inVerticalRange = playerBottom > obsTop + 5;

            if (!inVerticalRange) {
                continue;
            }

            if (velocityX > 0 && playerX < obsLeft) {
                player.setX(obsLeft - halfWidth - 1);
                player.stopMovingRight();
            }
            else if (velocityX < 0 && playerX > obsRight) {
                player.setX(obsRight + halfWidth + 1);
                player.stopMovingLeft();
            }
        }

        // Check platforms
        for (Platform platform : gameView.getPlatforms()) {
            Bounds platBounds = platform.getShape().getBoundsInParent();
            if (!playerBounds.intersects(platBounds)) continue;

            double platTop = platform.getTop();
            double platBottom = platform.getBottom();
            double platLeft = platform.getLeft();
            double platRight = platform.getRight();

            if (Math.abs(playerBottom - platTop) < 5 && player.isOnGround()) {
                continue;
            }

            boolean playerAbovePlatform = playerBottom <= platTop + 5;
            if (playerAbovePlatform) {
                continue;
            }

            boolean playerBelowPlatform = playerY >= platBottom - 5;
            if (playerBelowPlatform) {
                continue;
            }

            if (velocityX > 0 && playerX < platLeft) {
                player.setX(platLeft - halfWidth - 1);
                player.stopMovingRight();
            }
            else if (velocityX < 0 && playerX > platRight) {
                player.setX(platRight + halfWidth + 1);
                player.stopMovingLeft();
            }
        }
    }
}
