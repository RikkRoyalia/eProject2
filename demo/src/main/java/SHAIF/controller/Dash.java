package SHAIF.controller;

import SHAIF.model.Player;

public class Dash {
    public boolean dashing = false;
    private long dashEnd = 0;
    private long cooldownEnd = 0;

    public void tryDash(Player p) {
        long now = System.currentTimeMillis();

        if (now < cooldownEnd) return;
        if (dashing) return;

        dashing = true;
        dashEnd = now + 200;       // dash kéo dài 0.2s
        cooldownEnd = now + 1500; // cooldown 1.5s
    }

    public void update(Player p) {
        long now = System.currentTimeMillis();

        if (dashing) {
            p.speed = 18; // tốc độ dash
            if (now >= dashEnd) {
                dashing = false;
                p.speed = 5; // trả về tốc độ tam giác
            }
        }
    }
}
