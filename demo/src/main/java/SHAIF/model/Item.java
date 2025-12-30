package SHAIF.model;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Item implements InteractiveObjects {
    private final Circle shape;
    private ItemType itemType;
    private boolean collected;
    private RotateTransition rotateAnimation;

    public Item(double x, double y, ItemType itemType) {
        this.shape = new Circle(x, y, 8);
        this.itemType = itemType;
        this.collected = false;

        // Set style class dựa trên loại item
        setupItemStyle();

        // Tạo animation xoay
        setupAnimation();
    }

    private void setupItemStyle() {
        switch (itemType) {
            case HEALTH:
                shape.getStyleClass().add("item-health");
                break;
            case COIN:
                shape.getStyleClass().add("item-coin");
                break;
            case DASH_BOOST:
                shape.getStyleClass().add("item-dash");
                break;
            case SHIELD:
                shape.getStyleClass().add("item-shield");
                break;
            case SPEED_BOOST:
                shape.getStyleClass().add("item-speed");
                break;
            case DOUBLE_JUMP:
                shape.getStyleClass().add("item-jump");
                break;
            default:
                shape.getStyleClass().add("item");
        }
    }

    private void setupAnimation() {
        rotateAnimation = new RotateTransition(Duration.millis(2000), shape);
        rotateAnimation.setByAngle(360);
        rotateAnimation.setCycleCount(Animation.INDEFINITE);
        rotateAnimation.setAutoReverse(false);
        rotateAnimation.play();
    }

    // ===== InteractiveObjects Interface Implementation =====

    @Override
    public void activate() {
        collected = false;
        shape.setVisible(true);
        if (rotateAnimation != null) {
            rotateAnimation.play();
        }
    }

    @Override
    public void deactivate() {
        collected = true;
        shape.setVisible(false);
        if (rotateAnimation != null) {
            rotateAnimation.stop();
        }
    }

    @Override
    public boolean isActive() {
        return !collected;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean intersects(Shape other) {
        if (collected) return false;
        return shape.getBoundsInParent().intersects(other.getBoundsInParent());
    }

    @Override
    public void update() {
        // Animation được xử lý bởi RotateTransition
        // Có thể thêm các animation khác ở đây nếu cần
    }

    @Override
    public String getType() {
        return "ITEM_" + itemType.name();
    }

    // ===== Item specific methods =====

    public void collect() {
        deactivate();
    }

    public ItemType getItemType() {
        return itemType;
    }

    public boolean isCollected() {
        return collected;
    }

    public void stopAnimation() {
        if (rotateAnimation != null) {
            rotateAnimation.stop();
        }
    }
}
