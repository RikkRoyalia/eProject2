package SHAIF.model;

import javafx.scene.shape.*;
import java.util.List;

public class Player implements Movement {
    private FormType currentForm;
    private Shape currentShape;

    private double x;
    private double y;
    private double velY;
    private boolean onGround;
    private boolean movingLeft;
    private boolean movingRight;
    private boolean isDashing;

    // Constants
    private final int walkSpeed = 3;
    private final double gravity = 0.5;
    private final double jumpForce = -10;
    private double groundLevel;

    // Shapes
    private final Rectangle squareForm;
    private final Circle circleForm;
    private final Polygon triangleForm;
    private final Polygon leftTriangleForm;

    private int hitCount = 0;  // số lần trúng đạn
    private final int maxHits = 2; // trúng 2 lần sẽ game over


    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.velY = 0;
        this.onGround = false;
        this.isDashing = false;
        this.groundLevel = 380; // Default, sẽ được update từ GameView

        // Tạo các hình dạng với CSS classes
        squareForm = new Rectangle(-15, 0, 30, 30);
        squareForm.getStyleClass().add("player-square");

        circleForm = new Circle(0, 15, 15);
        circleForm.getStyleClass().add("player-circle");

        triangleForm = new Polygon(-15.0, 0.0, -15.0, 30.0, 15.0, 15.0);
        triangleForm.getStyleClass().add("player-triangle");

        leftTriangleForm = new Polygon(15.0, 0.0, 15.0, 30.0, -15.0, 15.0);
        leftTriangleForm.getStyleClass().add("player-triangle");

        currentForm = FormType.CIRCLE;
        currentShape = circleForm;
        updatePosition();
    }

    public double getHeight() {
        switch (currentForm) {
            case SQUARE: return squareForm.getHeight();
            case CIRCLE: return circleForm.getRadius() * 2;
            case TRIANGLE: return 30; // hoặc tính từ bounds
            case L_TRIANGLE: return 30;
            default: return 30;
        }
    }

    public double getWidth() {
        switch (currentForm) {
            case SQUARE: return squareForm.getWidth();
            case CIRCLE: return circleForm.getRadius() * 2;
            case TRIANGLE: return 30; // hoặc tính bounds
            case L_TRIANGLE: return 30;
            default: return 30;
        }
    }



    // ===== Movement Interface Implementation =====

    @Override
    public void applyGravity() {
        if (isDashing) {
            return;
        }

        velY += gravity;
        y += velY;

        if (y > groundLevel) {
            y = groundLevel;
            velY = 0;
            onGround = true;
        } else {
            onGround = false;
        }
        updatePosition();
    }

    // Method mới để xử lý gravity với platforms
    public void applyGravityWithPlatforms(List<Platform> platforms, double defaultGroundLevel) {
        if (isDashing) return;

        velY += gravity;
        y += velY;

        onGround = false; // reset trước khi check

        double playerHeight = getHeight(); // lấy chiều cao đúng của player

        // Kiểm tra va chạm với từng platform
        for (Platform platform : platforms) {
            if (velY > 0 && platform.isPlayerOnTop(x, y, 30, playerHeight)) {
                y = platform.getTop() - playerHeight;
                velY = 0;
                onGround = true;
                break;
            }
        }

        // Kiểm tra mặt đất nếu không đứng trên platform
        if (!onGround && y + playerHeight > defaultGroundLevel) {
            y = defaultGroundLevel - playerHeight;
            velY = 0;
            onGround = true;
        }

        updatePosition();
    }




    @Override
    public void update() {
        if (currentForm == FormType.CIRCLE) {
            if (movingLeft) {
                x -= walkSpeed;
            }
            if (movingRight) {
                x += walkSpeed;
            }
            updatePosition();
        }
    }

    @Override
    public void moveLeft() {
        movingLeft = true;
    }

    @Override
    public void moveRight() {
        movingRight = true;
    }

    @Override
    public void jump() {
        if (onGround) {
            velY = jumpForce;
            onGround = false;
        }
    }

    @Override
    public void stop() {
        movingLeft = false;
        movingRight = false;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setX(double x) {
        this.x = x;
        updatePosition();
    }

    @Override
    public void setY(double y) {
        this.y = y;
        updatePosition();
    }

    @Override
    public boolean isOnGround() {
        return onGround;
    }

    // ===== Player specific methods =====

    public void switchForm(FormType form) {
        currentForm = form;

        switch (form) {
            case SQUARE:
                currentShape = squareForm;
                break;
            case CIRCLE:
                currentShape = circleForm;
                break;
            case TRIANGLE:
                currentShape = triangleForm;
                break;
            case L_TRIANGLE:
                currentShape = leftTriangleForm;
                break;
        }

        updatePosition();
    }

    private void updatePosition() {
        squareForm.setTranslateX(x);
        squareForm.setTranslateY(y);
        circleForm.setTranslateX(x);
        circleForm.setTranslateY(y);
        triangleForm.setTranslateX(x);
        triangleForm.setTranslateY(y);
        leftTriangleForm.setTranslateX(x);
        leftTriangleForm.setTranslateY(y);
    }

    public void takeDamage() {
        hitCount++;
        currentShape.getStyleClass().clear();
        currentShape.getStyleClass().add("player-damaged");
    }
    public boolean isDead() {
        return hitCount >= maxHits;
    }

    public void stopMovingLeft() {
        movingLeft = false;
    }

    public void stopMovingRight() {
        movingRight = false;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setDashing(boolean dashing) {
        this.isDashing = dashing;
    }

    public boolean isDashing() {
        return isDashing;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public void setGroundLevel(double groundLevel) {
        this.groundLevel = groundLevel;
    }

    // Getters
    public Shape getCurrentShape() { return currentShape; }
    public FormType getCurrentForm() { return currentForm; }
    public Rectangle getSquareForm() { return squareForm; }
    public Circle getCircleForm() { return circleForm; }
    public Polygon getTriangleForm() { return triangleForm; }
    public Polygon getLeftTriangleForm() { return leftTriangleForm; }

}