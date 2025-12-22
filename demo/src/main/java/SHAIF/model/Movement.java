package SHAIF.model;

public interface Movement {
    /**
     * Áp dụng trọng lực cho object
     */
    void applyGravity();

    /**
     * Cập nhật vị trí của object
     */
    void update();

    /**
     * Di chuyển sang trái
     */
    void moveLeft();

    /**
     * Di chuyển sang phải
     */
    void moveRight();

    /**
     * Nhảy
     */
    void jump();

    /**
     * Dừng di chuyển
     */
    void stop();

    /**
     * Lấy vị trí X hiện tại
     */
    double getX();

    /**
     * Lấy vị trí Y hiện tại
     */
    double getY();

    /**
     * Set vị trí X
     */
    void setX(double x);

    /**
     * Set vị trí Y
     */
    void setY(double y);

    /**
     * Kiểm tra có đang đứng trên mặt đất không
     */
    boolean isOnGround();
}