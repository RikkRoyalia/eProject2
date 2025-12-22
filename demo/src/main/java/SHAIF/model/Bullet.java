package SHAIF.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class Bullet implements InteractiveObjects {
    private final Circle shape;
    private boolean alive;
    private final int speed = 4;

    public Bullet() {
        shape = new Circle(5);
        shape.setFill(Color.BLACK);
        shape.setVisible(false);
        alive = false;
    }

    // ===== InteractiveObjects Interface Implementation =====

    @Override
    public void activate() {
        alive = true;
        shape.setVisible(true);
    }

    @Override
    public void deactivate() {
        alive = false;
        shape.setVisible(false);
    }

    @Override
    public boolean isActive() {
        return alive;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean intersects(Shape other) {
        if (!alive) return false;
        return shape.getBoundsInParent().intersects(other.getBoundsInParent());
    }

    @Override
    public void update() {
        if (!alive) return;

        shape.setCenterX(shape.getCenterX() - speed);

        // Nếu bay ra khỏi màn hình
        if (shape.getCenterX() < 0) {
            deactivate();
        }
    }

    @Override
    public String getType() {
        return "BULLET";
    }

    // ===== Bullet specific methods =====

    public void shoot(double startX, double startY) {
        activate();
        shape.setCenterX(startX);
        shape.setCenterY(startY);
    }

    public boolean isAlive() {
        return alive;
    }
}