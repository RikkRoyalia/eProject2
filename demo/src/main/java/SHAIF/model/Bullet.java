package SHAIF.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bullet {

    private final Circle bullet = new Circle(5);
    private boolean alive = false;

    public Bullet() {
        bullet.setFill(Color.BLACK);
        bullet.setVisible(false);
    }

    public void shoot(double x, double y) {
        alive = true;
        bullet.setVisible(true);
        bullet.setCenterX(x);
        bullet.setCenterY(y);
    }

    public void update() {
        if (!alive) return;
        bullet.setCenterX(bullet.getCenterX() - 4);

        if (bullet.getCenterX() < 0) {
            alive = false;
            bullet.setVisible(false);
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public Circle getShape() {
        return bullet;
    }

    public void destroy() {
        alive = false;
        bullet.setVisible(false);
    }
}
