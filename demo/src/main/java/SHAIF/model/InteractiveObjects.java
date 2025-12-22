package SHAIF.model;

import javafx.scene.shape.Shape;

public interface InteractiveObjects {
    /**
     * Kích hoạt object
     */
    void activate();

    /**
     * Vô hiệu hóa object
     */
    void deactivate();

    /**
     * Kiểm tra object có đang active không
     */
    boolean isActive();

    /**
     * Lấy shape để render
     */
    Shape getShape();

    /**
     * Kiểm tra va chạm với object khác
     */
    boolean intersects(Shape other);

    /**
     * Cập nhật trạng thái object (được gọi mỗi frame)
     */
    void update();

    /**
     * Lấy loại object
     */
    String getType();
}