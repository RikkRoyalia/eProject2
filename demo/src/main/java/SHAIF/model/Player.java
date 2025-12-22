package SHAIF.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class Player implements Movement {
    private FormType currentForm;
    private Shape currentShape;

    private double x;
    private double y;
    private double velY;
    private boolean onGround;
    private boolean movingLeft;
    private boolean movingRight;

    // Constants
    private final int walkSpeed = 3;
    private final double gravity = 0.5;
    private final double jumpForce = -12;
    private final double groundLevel = 380;

    // Shapes
    private final Rectangle squareForm;
    private final Circle circleForm;
    private final Polygon triangleForm;

    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.velY = 0;
        this.onGround = false;

        // Tạo các hình dạng
        squareForm = new Rectangle(30, 30);
        squareForm.setFill(Color.DARKBLUE);

        circleForm = new Circle(0, 15, 15);
        circleForm.setFill(Color.DARKRED);

        triangleForm = new Polygon(0.0, 0.0, 0.0, 30.0, 30.0, 15.0);
        triangleForm.setFill(Color.DARKGREEN);

        // Khởi tạo form ban đầu (chưa thêm vào scene)
        currentForm = FormType.CIRCLE;
        currentShape = circleForm;
        updatePosition();
    }

    // ===== Movement Interface Implementation =====

    @Override
    public void applyGravity() {
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

    @Override
    public void update() {
        // Chỉ di chuyển ngang khi là Circle
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
        }

        // Cập nhật vị trí cho shape mới
        updatePosition();
    }

    private void updatePosition() {
        // Cập nhật TẤT CẢ các shape để đồng bộ vị trí
        squareForm.setTranslateX(x);
        squareForm.setTranslateY(y);
        circleForm.setTranslateX(x);
        circleForm.setTranslateY(y);
        triangleForm.setTranslateX(x);
        triangleForm.setTranslateY(y);
    }

    public void takeDamage() {
        currentShape.setFill(Color.RED);
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

    // Getters
    public Shape getCurrentShape() { return currentShape; }
    public FormType getCurrentForm() { return currentForm; }
    public Rectangle getSquareForm() { return squareForm; }
    public Circle getCircleForm() { return circleForm; }
    public Polygon getTriangleForm() { return triangleForm; }
}