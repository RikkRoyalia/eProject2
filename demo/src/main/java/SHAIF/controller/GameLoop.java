package SHAIF.controller;

import javafx.animation.AnimationTimer;
import SHAIF.model.*;

public class GameLoop extends AnimationTimer {

    private final Player player;
    private final Enemy enemy;
    private final Bullet bullet;
    private final KeyInput input;
    private long lastShoot = 0;

    public GameLoop(Player p, Enemy e, Bullet b, KeyInput i) {
        player = p;
        enemy = e;
        bullet = b;
        input = i;
    }

    @Override
    public void handle(long now) {
        player.applyGravity();
        input.update(player);
        bullet.update();

        if (now - lastShoot > 1_500_000_000L && enemy.isAlive()) {
            bullet.shoot(enemy.getBody().getX(), enemy.getBody().getY() + 20);
            lastShoot = now;
        }
    }
}
