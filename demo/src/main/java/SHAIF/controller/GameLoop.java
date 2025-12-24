package SHAIF.controller;

import SHAIF.model.*;
import SHAIF.screen.MenuScreen;
import SHAIF.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.Rectangle;
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

    private void gameOver() {
        // Dừng vòng lặp game
        if (timer != null) timer.stop();

        // Chạy UI thread
        javafx.application.Platform.runLater(() -> {
            // Hiển thị thông báo
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION
            );
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("Game Over!");
            alert.showAndWait();

            // Quay về menu
            menuScreen.show();
            primaryStage.setScene(menuScreen.getScene());
        });
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

// Bị trúng đạn quá số lần
        if (bullet.intersects(player.getCurrentShape()) && player.getCurrentForm() != FormType.SQUARE) {
            player.takeDamage();
            if (player.isDead()) {
                gameOver();
                return;
            }
        }
    }
}