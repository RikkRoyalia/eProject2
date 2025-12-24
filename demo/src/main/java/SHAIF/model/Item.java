package SHAIF.model;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class Item implements InteractiveObjects {
    private final Circle shape;
    private String itemType;
    private boolean collected;

    public Item(double x, double y, String itemType) {
        this.shape = new Circle(x, y, 8);
        this.shape.getStyleClass().add("item");
        this.itemType = itemType;
        this.collected = false;
    }

    // ===== InteractiveObjects Interface Implementation =====

    @Override
    public void activate() {
        collected = false;
        shape.setVisible(true);
    }

    @Override
    public void deactivate() {
        collected = true;
        shape.setVisible(false);
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
        // Items có thể có animation nhấp nháy, xoay, v.v.
        // Tạm thời để trống
    }

    @Override
    public String getType() {
        return "ITEM_" + itemType;
    }

    // ===== Item specific methods =====

    public void collect() {
        deactivate();
    }

    public String getItemType() {
        return itemType;
    }

    public boolean isCollected() {
        return collected;
    }
}