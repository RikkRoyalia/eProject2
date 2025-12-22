package SHAIF.controller;

import SHAIF.model.Player;
import SHAIF.model.FormType;
import SHAIF.view.GameView;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class KeyInput {
    private final Player player;
    private final DashController dashController;
    private final GameView gameView;

    public KeyInput(Player player, DashController dashController, GameView gameView) {
        this.player = player;
        this.dashController = dashController;
        this.gameView = gameView;
    }

    public void setupInput(Scene scene) {
        scene.setOnKeyPressed(e -> {
            // Di chuyển
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                player.moveLeft();
            }
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                player.moveRight();
            }

            // Nhảy
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) {
                player.jump();
            }

            // Chuyển hình dạng - QUAN TRỌNG: phải remove shape cũ và add shape mới
            if (e.getCode() == KeyCode.U) {
                gameView.removeNode(player.getCurrentShape());
                player.switchForm(FormType.CIRCLE);
                gameView.addNode(player.getCurrentShape());
            }
            if (e.getCode() == KeyCode.I) {
                gameView.removeNode(player.getCurrentShape());
                player.switchForm(FormType.TRIANGLE);
                gameView.addNode(player.getCurrentShape());
                dashController.startDash();
            }
            if (e.getCode() == KeyCode.O) {
                gameView.removeNode(player.getCurrentShape());
                player.switchForm(FormType.SQUARE);
                gameView.addNode(player.getCurrentShape());
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                player.stopMovingLeft();
            }
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                player.stopMovingRight();
            }
        });
    }
}