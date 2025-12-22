package SHAIF.controller;

import SHAIF.model.Player;
import SHAIF.model.InteractiveObjects;

public class DashController {
    private final Player player;

    private boolean isDashing;
    private int dashDistanceLeft;
    private int dashDir; // 1 = phải, -1 = trái

    private final int dashSpeed = 10;
    private final int dashDistance = 60;

    public DashController(Player player) {
        this.player = player;
        this.isDashing = false;
        this.dashDistanceLeft = 0;
        this.dashDir = 1;
    }

    public void startDash() {
        if (isDashing) return;

        isDashing = true;
        dashDistanceLeft = dashDistance;

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
            isDashing = false;
        }
    }

    public void stopDash() {
        isDashing = false;
        dashDistanceLeft = 0;
    }

    public boolean isDashing() {
        return isDashing;
    }

    public boolean checkCollision(InteractiveObjects object) {
        if (!isDashing) return false;
        return object.intersects(player.getCurrentShape());
    }
}