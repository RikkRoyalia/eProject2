package SHAIF.view;

import javafx.animation.FadeTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Hiển thị mũi tên chỉ hướng có thể chuyển room
 */
public class TransitionIndicator {
    private Pane root;
    private Polygon leftArrow;
    private Polygon rightArrow;
    private Polygon topArrow;
    private Polygon bottomArrow;

    public TransitionIndicator(Pane root, double screenWidth, double screenHeight) {
        this.root = root;

        // Left arrow
        leftArrow = createArrow(20, screenHeight / 2, "LEFT");

        // Right arrow
        rightArrow = createArrow(screenWidth - 20, screenHeight / 2, "RIGHT");

        // Top arrow
        topArrow = createArrow(screenWidth / 2, 20, "TOP");

        // Bottom arrow
        bottomArrow = createArrow(screenWidth / 2, screenHeight - 20, "BOTTOM");

        hideAll();
    }

    private Polygon createArrow(double x, double y, String direction) {
        Polygon arrow = new Polygon();

        switch (direction) {
            case "LEFT":
                arrow.getPoints().addAll(
                        0.0, 0.0,
                        20.0, -15.0,
                        20.0, 15.0
                );
                break;
            case "RIGHT":
                arrow.getPoints().addAll(
                        0.0, 0.0,
                        -20.0, -15.0,
                        -20.0, 15.0
                );
                break;
            case "TOP":
                arrow.getPoints().addAll(
                        0.0, 0.0,
                        -15.0, 20.0,
                        15.0, 20.0
                );
                break;
            case "BOTTOM":
                arrow.getPoints().addAll(
                        0.0, 0.0,
                        -15.0, -20.0,
                        15.0, -20.0
                );
                break;
        }

        arrow.setTranslateX(x);
        arrow.setTranslateY(y);
        arrow.setFill(Color.CYAN);
        arrow.setStroke(Color.WHITE);
        arrow.setStrokeWidth(2);
        arrow.setOpacity(0);

        // Pulsing animation
        FadeTransition fade = new FadeTransition(Duration.seconds(1), arrow);
        fade.setFromValue(0.3);
        fade.setToValue(1.0);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();

        root.getChildren().add(arrow);
        return arrow;
    }

    public void showDirection(String direction) {
        hideAll();

        switch (direction.toUpperCase()) {
            case "LEFT":
                leftArrow.setVisible(true);
                break;
            case "RIGHT":
                rightArrow.setVisible(true);
                break;
            case "TOP":
                topArrow.setVisible(true);
                break;
            case "BOTTOM":
                bottomArrow.setVisible(true);
                break;
        }
    }

    public void hideAll() {
        leftArrow.setVisible(false);
        rightArrow.setVisible(false);
        topArrow.setVisible(false);
        bottomArrow.setVisible(false);
    }
}