package SHAIF.game;

import SHAIF.controller.KeyInput;
import SHAIF.model.Player;
import SHAIF.model.Projectile;
import javafx.animation.AnimationTimer;

class GameLoop extends AnimationTimer {

    @Override
    public void handle(long now) {
        Player p = Game.player;
        KeyInput i = Game.input;

        double dx = 0, dy = 0;
        if (i.up)    dy -= 1;
        if (i.down)  dy += 1;
        if (i.left)  dx -= 1;
        if (i.right) dx += 1;

        double len = Math.sqrt(dx*dx + dy*dy);
        if (len != 0) {
            dx /= len;
            dy /= len;
        }

        // Di chuyển player
        p.x += dx * p.speed;
        p.y += dy * p.speed;

        // Dash
        if (p.form == Player.Form.TRIANGLE && i.dash)
            Game.dash.tryDash(p);

        Game.dash.update(p);
        p.updatePos();

        // Bắn
        if (p.form == Player.Form.STAR && i.shoot) {
            Game.spawnProjectile();
        }

        // Update đạn
        for (Projectile pr : Game.projectiles)
            pr.update();

        // Enemy
        Game.enemy.update(p);
    }
}

