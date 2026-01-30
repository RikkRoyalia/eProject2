package SHAIF.controller;

import SHAIF.model.Player;
import SHAIF.model.FormType;
import SHAIF.screen.PauseScreen;
import SHAIF.view.GameView;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class KeyInput {
    private final Player player;
    private final DashController dashController;
    private final GameView gameView;
    private PauseScreen pauseScreen;
    private Runnable onPauseCallback;
    private ComboSystem comboSystem;

    public KeyInput(Player player, DashController dashController, GameView gameView) {
        this.player = player;
        this.dashController = dashController;
        this.gameView = gameView;
    }

    public void setPauseScreen(PauseScreen pauseScreen) {
        this.pauseScreen = pauseScreen;
    }

    public void setOnPauseCallback(Runnable callback) {
        this.onPauseCallback = callback;
    }

    public void setComboSystem(ComboSystem comboSystem) {
        this.comboSystem = comboSystem;
    }

    public void setupInput(Scene scene) {
        scene.setOnKeyPressed(e -> {
            // Di chuyển
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                if (!player.isKnockedBack()) {
                    player.moveLeft();
                }
            }
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                if (!player.isKnockedBack()) {
                    player.moveRight();
                }
            }

            // Nhảy
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) {
                player.jump();
            }

            // Chuyển hình dạng
            // I - Chuyển sang tam giác và dash, sẽ tự động về tròn khi dash kết thúc
            if (e.getCode() == KeyCode.I) {
                // Chọn hướng tam giác dựa vào hướng di chuyển
                if (player.isMovingLeft()) {
                    switchToShape(FormType.L_TRIANGLE);  // Tam giác trái
                } else {
                    switchToShape(FormType.TRIANGLE);     // Tam giác phải (mặc định)
                }
                dashController.startDash();
            }

            // O - Chuyển sang vuông (chỉ khi giữ phím)
            if (e.getCode() == KeyCode.O) {
                switchToShape(FormType.SQUARE);
            }

            // ESC hoặc P - Pause
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.P) {
                if (onPauseCallback != null) {
                    onPauseCallback.run();
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                player.stopMovingLeft();
            }
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                player.stopMovingRight();
            }

            // O released - Chuyển về hình tròn ngay lập tức
            if (e.getCode() == KeyCode.O) {
                switchToShape(FormType.CIRCLE);
            }
        });
    }

    // Helper method để chuyển đổi hình dạng
    private void switchToShape(FormType formType) {
        gameView.removeNode(player.getCurrentShape());
        player.switchForm(formType);
        gameView.addNode(player.getCurrentShape());

        // Record shape change for combo system
        if (comboSystem != null) {
            comboSystem.recordShapeChange(formType);
        }
    }
}