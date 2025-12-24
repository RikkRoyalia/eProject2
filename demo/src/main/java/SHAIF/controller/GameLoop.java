package SHAIF.controller;

import SHAIF.model.*;
import SHAIF.screen.MenuScreen;
import SHAIF.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

public class GameLoop {

    private AnimationTimer timer;

    private final GameView gameView;
    private final Player player;
    private final Enemy enemy;
    private final Bullet bullet;
    private final DashController dashController;
    private final Stage primaryStage;
    private final MenuScreen menuScreen;

    public GameLoop(GameView gameView, Player player, Enemy enemy, Bullet bullet,
                    DashController dashController, Stage primaryStage, MenuScreen menuScreen) {
        this.gameView = gameView;
        this.player = player;
        this.enemy = enemy;
        this.bullet = bullet;
        this.dashController = dashController;
        this.primaryStage = primaryStage;
        this.menuScreen = menuScreen;
    }

    public void start() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Cập nhật game
                player.applyGravityWithPlatforms(gameView.getPlatforms(), gameView.getGroundLevel());
                player.update();
                dashController.update();

                if (enemy.shouldShoot(now)) {
                    bullet.shoot(enemy.getX(), enemy.getY() + 20);
                }

                enemy.update();
                bullet.update();

                checkCollisions();
            }
        };
        timer.start();
    }


    private void checkCollisions() {
        // Player dash vào enemy (dùng interface method)
        if (dashController.checkCollision(enemy)) {
            enemy.defeat();
            dashController.stopDash();
        }

        // Bullet trúng player (dùng interface method)
        if (bullet.intersects(player.getCurrentShape())) {
            if (player.getCurrentForm() == FormType.SQUARE) {
                // Square chặn đạn
                bullet.deactivate();
            } else {
                // Nhận damage
                player.takeDamage();
            }
        }

        // Chạm đích
        if (player.getCurrentShape().getBoundsInParent()
                .intersects(gameView.getGoal().getBoundsInParent())) {
            System.out.println("Bạn đã hoàn thành màn!");
            System.exit(0);
        }
    }
}