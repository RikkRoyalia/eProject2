package SHAIF.controller;

import SHAIF.game.Game;
import SHAIF.model.Player;
import javafx.scene.Scene;

import java.awt.event.KeyAdapter;

public class KeyInput extends KeyAdapter {
    public boolean up, down, left, right;
    public boolean dash;
    public boolean shoot;
    public double mouseX, mouseY;

    public void bind(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> up = true;
                case S -> down = true;
                case A -> left = true;
                case D -> right = true;

                case DIGIT1 -> Game.player.setForm(Player.Form.CIRCLE);
                case DIGIT2 -> Game.player.setForm(Player.Form.SQUARE);
                case DIGIT3 -> Game.player.setForm(Player.Form.TRIANGLE);
                case DIGIT4 -> Game.player.setForm(Player.Form.STAR);

                case SPACE -> dash = true;
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> up = false;
                case S -> down = false;
                case A -> left = false;
                case D -> right = false;
                case SPACE -> dash = false;
            }
        });

        scene.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        scene.setOnMouseDragged(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        scene.setOnMousePressed(e -> shoot = true);
        scene.setOnMouseReleased(e -> shoot = false);
    }
}
