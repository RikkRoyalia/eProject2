package SHAIF.model;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Enemy implements InteractiveObjects {
    private final Rectangle shape;
    private boolean active;
    private long lastShootTime;

    private final long shootInterval = 1_500_000_000L; // 1.5 giây

    public Enemy(double x, double y) {
        shape = new Rectangle(30, 40);
        shape.getStyleClass().add("enemy");
        shape.setX(x);
        shape.setY(y);
        active = true;
        lastShootTime = 0;
    }

    // ===== InteractiveObjects Interface Implementation =====

    @Override
    public void activate() {
        active = true;
        shape.setDisable(false);
        shape.getStyleClass().clear();
        shape.getStyleClass().add("enemy");
    }

    @Override
    public void deactivate() {
        active = false;
        shape.setDisable(true);
        shape.getStyleClass().clear();
        shape.getStyleClass().add("enemy-defeated");
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean intersects(Shape other) {
        return shape.getBoundsInParent().intersects(other.getBoundsInParent());
    }

    @Override
    public void update() {
        // Enemy không cần update mỗi frame
        // Logic bắn đạn được xử lý ở GameLoop
    }

    @Override
    public String getType() {
        return "ENEMY";
    }

    // ===== Enemy specific methods =====

    public boolean shouldShoot(long currentTime) {
        if (!active) return false;

        if (currentTime - lastShootTime > shootInterval) {
            lastShootTime = currentTime;
            return true;
        }
        return false;
    }

    public void defeat() {
        deactivate();
    }

    public double getX() {
        return shape.getX();
    }

    public double getY() {
        return shape.getY();
    }
}