package SHAIF.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import SHAIF.model.Player;
import SHAIF.model.FormType;

public class KeyInput {

    private boolean left, right;

    public KeyInput(Scene scene, Player player, DashController dash) {

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A) left = true;
            if (e.getCode() == KeyCode.D) right = true;
            if (e.getCode() == KeyCode.W) player.jump();

            if (e.getCode() == KeyCode.U) player.switchForm(FormType.CIRCLE);
            if (e.getCode() == KeyCode.O) player.switchForm(FormType.SQUARE);
            if (e.getCode() == KeyCode.I) {
                player.switchForm(FormType.TRIANGLE);
                dash.dash();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.A) left = false;
            if (e.getCode() == KeyCode.D) right = false;
        });
    }

    public void update(Player player) {
        if (left) player.move(-3);
        if (right) player.move(3);
    }
}
