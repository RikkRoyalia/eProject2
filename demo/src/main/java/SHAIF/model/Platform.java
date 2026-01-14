package SHAIF.model;

import javafx.scene.shape.Rectangle;

public class Platform {
    private final Rectangle shape;
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public Platform(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.shape = new Rectangle(width, height);
        this.shape.setX(x);
        this.shape.setY(y);
        this.shape.getStyleClass().add("platform");
    }

    public Rectangle getShape() {
        return shape;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getTop() {
        return y;
    }

    public double getBottom() {
        return y + height;
    }

    public double getLeft() {
        return x;
    }

    public double getRight() {
        return x + width;
    }

    // Kiểm tra player có đang đứng trên platform không
    public boolean isPlayerOnTop(double playerX, double playerY, double playerWidth, double playerHeight) {
        double playerBottom = playerY + playerHeight;
        double playerLeft = playerX - playerWidth / 2;
        double playerRight = playerX + playerWidth / 2;

        // Player phải ở trên platform
        boolean isAbove = playerBottom <= getTop() + 5 && playerBottom >= getTop() - 5;

        // Player phải có phần nào đó nằm trong khoảng x của platform
        boolean isOverlapping = playerRight > getLeft() && playerLeft < getRight();

        return isAbove && isOverlapping;
    }
}