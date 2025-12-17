package SHAIF.controller;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import SHAIF.model.Enemy;
import SHAIF.model.Player;

public class DashController {

    private final Player player;
    private final Enemy enemy;

    private boolean canDash = true;

    public DashController(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public void dash() {
        if (!canDash) return;
        canDash = false;

        var shape = player.getShape();

        ScaleTransition compress = new ScaleTransition(Duration.millis(70), shape);
        compress.setToY(0.85);

        TranslateTransition dashMove = new TranslateTransition(Duration.millis(120), shape);
        dashMove.setByX(40);
        dashMove.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition relax = new ScaleTransition(Duration.millis(70), shape);
        relax.setToY(1.0);

        dashMove.setOnFinished(e -> {
            player.move(40);

            if (shape.getBoundsInParent()
                    .intersects(enemy.getBody().getBoundsInParent())) {
                enemy.kill();
            }
            canDash = true;
        });

        new SequentialTransition(
                compress,
                dashMove,
                relax
        ).play();
    }
}
