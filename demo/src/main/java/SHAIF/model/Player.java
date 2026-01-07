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
    private double minX = 0;
    private double maxX = 1280;
    private double minY = 0;
    private double maxY = 720;

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

    // Power-ups
    private boolean hasShield = false;
    private long shieldEndTime = 0;
    private boolean hasSpeedBoost = false;
    private long speedBoostEndTime = 0;
    private boolean hasDoubleJump = false;
    private int jumpCount = 0;
    private boolean hasDashBoost = false;
    private long dashBoostEndTime = 0;

    private int currentWalkSpeed = walkSpeed;

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

    /**
     * Set screen bounds để giới hạn player
     */
    public void setScreenBounds(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * Áp dụng bounds khi update position
     */
    private void applyBounds() {
        // Giới hạn X
        if (x < minX) {
            x = minX;
        }
        if (x > maxX) {
            x = maxX;
        }

        // Giới hạn Y (chỉ trên, không giới hạn dưới vì có gravity)
        if (y < minY) {
            y = minY;
        }
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
        // Update power-ups
        updatePowerUps();

        if (currentForm == FormType.CIRCLE) {
            if (movingLeft) {
                x -= currentWalkSpeed;
            }
            if (movingRight) {
                x += currentWalkSpeed;
            }
            // Áp dụng bounds
            applyBounds();
            updatePosition();
        }
    }

    private void updatePowerUps() {
        long currentTime = System.nanoTime();

        // Shield
        if (hasShield && currentTime > shieldEndTime) {
            hasShield = false;
        }

        // Speed boost
        if (hasSpeedBoost && currentTime > speedBoostEndTime) {
            hasSpeedBoost = false;
            currentWalkSpeed = walkSpeed;
        }

        // Dash boost
        if (hasDashBoost && currentTime > dashBoostEndTime) {
            hasDashBoost = false;
        }

        // Reset jump count khi chạm đất
        if (onGround) {
            jumpCount = 0;
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
            jumpCount = 1;
        } else if (hasDoubleJump && jumpCount < 2) {
            // Double jump
            velY = jumpForce;
            jumpCount++;
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
        applyBounds();
        updatePosition();
    }

    @Override
    public void setY(double y) {
        this.y = y;
        applyBounds();
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
        if (hasShield()) {
            // Shield bảo vệ khỏi damage
            return;
        }

        hitCount++;
        currentShape.getStyleClass().clear();
        currentShape.getStyleClass().add("player-damaged");
    }

    public void heal() {
        if (hitCount > 0) {
            hitCount--;
            // Reset style về bình thường
            switch (currentForm) {
                case SQUARE:
                    currentShape.getStyleClass().clear();
                    currentShape.getStyleClass().add("player-square");
                    break;
                case CIRCLE:
                    currentShape.getStyleClass().clear();
                    currentShape.getStyleClass().add("player-circle");
                    break;
                case TRIANGLE:
                case L_TRIANGLE:
                    currentShape.getStyleClass().clear();
                    currentShape.getStyleClass().add("player-triangle");
                    break;
            }
        }
    }

    // Power-up methods
    public void activateShield(long durationSeconds) {
        hasShield = true;
        shieldEndTime = System.nanoTime() + (durationSeconds * 1_000_000_000L);
    }

    public void activateSpeedBoost(long durationSeconds) {
        hasSpeedBoost = true;
        speedBoostEndTime = System.nanoTime() + (durationSeconds * 1_000_000_000L);
        currentWalkSpeed = walkSpeed * 2; // Tăng gấp đôi
    }

    public void activateDoubleJump() {
        hasDoubleJump = true;
    }

    public void activateDashBoost(long durationSeconds) {
        hasDashBoost = true;
        dashBoostEndTime = System.nanoTime() + (durationSeconds * 1_000_000_000L);
    }

    public boolean hasDashBoost() {
        return hasDashBoost && System.nanoTime() <= dashBoostEndTime;
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
    public int getHitCount() { return hitCount; }
    public int getMaxHits() { return maxHits; }
    public boolean hasShield() { return hasShield && System.nanoTime() <= shieldEndTime; }
    public boolean hasSpeedBoost() { return hasSpeedBoost && System.nanoTime() <= speedBoostEndTime; }
    public boolean hasDoubleJump() { return hasDoubleJump; }
}
