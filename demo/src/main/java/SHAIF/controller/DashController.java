package SHAIF.controller;

import SHAIF.model.Player;
import SHAIF.model.FormType;
import SHAIF.model.InteractiveObjects;
import SHAIF.view.GameView;

public class DashController {
    private final Player player;
    private final GameView gameView;
    private Sound sound;

    private boolean isDashing;
    private int dashDistanceLeft;
    private int dashDir; // 1 = phải, -1 = trái

    private final int dashSpeed = 10;
    private final int dashDistance = 60;

    public DashController(Player player, GameView gameView) {
        this.player = player;
        this.gameView = gameView;
        this.isDashing = false;
        this.dashDistanceLeft = 0;
        this.dashDir = 1;
        this.sound = new Sound();
    }

    public void startDash() {
        if (isDashing) return;

        isDashing = true;
        dashDistanceLeft = dashDistance;
        player.setDashing(true); // Bật flag dash cho player
        player.setVelY(0); // Cancel velocity từ nhảy

        //dash's sound
        sound.playSE(5);

        // Xác định hướng dash dựa vào movement của player
        if (player.isMovingLeft()) {
            dashDir = -1;
        } else {
            dashDir = 1;
        }
    }

    public void update() {
        if (!isDashing) return;

        double newX = player.getX() + (dashDir * dashSpeed);
        player.setX(newX);

        dashDistanceLeft -= dashSpeed;

        if (dashDistanceLeft <= 0) {
            endDash();
        }
    }

    private void endDash() {
        isDashing = false;
        dashDistanceLeft = 0;
        player.setDashing(false); // Tắt flag dash cho player

        // Tự động chuyển về hình tròn khi dash kết thúc
        gameView.removeNode(player.getCurrentShape());
        player.switchForm(FormType.CIRCLE);
        gameView.addNode(player.getCurrentShape());
    }

    public void stopDash() {
        if (isDashing) {
            endDash();
        }
    }

    public boolean isDashing() {
        return isDashing;
    }

    public boolean checkCollision(InteractiveObjects object) {
        if (!isDashing) return false;
        return object.intersects(player.getCurrentShape());
    }
}