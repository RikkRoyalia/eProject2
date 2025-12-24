package SHAIF.controller;

import SHAIF.model.*;
import SHAIF.view.GameView;
import javafx.animation.AnimationTimer;

public class GameLoop {
    private final GameView gameView;
    private final Player player;
    private final Enemy enemy;
    private final Bullet bullet;
    private final DashController dashController;

    public GameLoop(GameView gameView, Player player, Enemy enemy, Bullet bullet,
                    DashController dashController) {
        this.gameView = gameView;
        this.player = player;
        this.enemy = enemy;
        this.bullet = bullet;
        this.dashController = dashController;
    }

    public void start() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Cập nhật player với platform collision
                player.applyGravityWithPlatforms(gameView.getPlatforms(), gameView.getGroundLevel());
                player.update();

                // Cập nhật dash
                dashController.update();

                // Enemy bắn đạn
                if (enemy.shouldShoot(now)) {
                    bullet.shoot(enemy.getX(), enemy.getY() + 20);
                }

                // Cập nhật các InteractiveObjects
                enemy.update();
                bullet.update();

                // Kiểm tra va chạm
                checkCollisions();
            }
        }.start();
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