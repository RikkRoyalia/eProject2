package SHAIF.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Enemy {

    private final Rectangle body = new Rectangle(30, 40);

    public Enemy() {
        body.setFill(Color.PURPLE);
        body.setX(600);
        body.setY(300);
    }

    public Rectangle getBody() {
        return body;
    }

    public boolean isAlive() {
        return !body.isDisable();
    }

    public void kill() {
        body.setFill(Color.PINK);
        body.setDisable(true);
    }
}
