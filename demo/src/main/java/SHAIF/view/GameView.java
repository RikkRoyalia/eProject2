package SHAIF.view;

import SHAIF.model.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;

public class GameView {
    private final Pane root;
    private Rectangle goal;
    private final List<Platform> platforms;

    private final List<Rectangle> pits;
    private final double screenWidth;
    private final double screenHeight;
    private final double groundLevel;

    public GameView() {
//        // Lấy kích thước màn hình
//        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//        screenWidth = screenBounds.getWidth();
//        screenHeight = screenBounds.getHeight();
//        groundLevel = screenHeight - 150;
//        root = new Pane();
//        root.setPrefSize(screenWidth, screenHeight);
//        root.getStyleClass().add("game-root");
//
//        platforms = new ArrayList<>();
//
//        setupPlatforms();
//        setupGoal();

        screenWidth = 1280;
        screenHeight = 720;
        groundLevel = 680; // mặt trên của mặt đất

        root = new Pane();
        root.setPrefSize(screenWidth, screenHeight);
        root.getStyleClass().add("game-root");

        platforms = new ArrayList<>();
        pits = new ArrayList<>();

        setupPlatforms();
        setupGoal();
    }

    private void setupPlatforms() {
        Rectangle ground = new Rectangle(screenWidth, 40);
        ground.getStyleClass().add("platform");
        ground.setY(groundLevel);
        root.getChildren().add(ground);

        // Tầng 1: Platforms thấp
        Platform low1 = new Platform(550,   180, 150, 20);
        Platform low2 = new Platform(600,   260, 150, 20);

        // Tầng 2: Platforms trung bình
        Platform mid1 = new Platform(550,   340, 150, 20);
        Platform mid2 = new Platform(600,   420, 150, 20);

        // Tầng 3: Platforms cao
        Platform high1 = new Platform(800,   500, 150, 20);
        Platform high2 = new Platform(750,   580, 150, 20);

        // Thêm tất cả vào list
        platforms.add(low1);
        platforms.add(low2);
        platforms.add(mid1);
        platforms.add(mid2);
        platforms.add(high1);
        platforms.add(high2);

        for (Platform p : platforms) {
            root.getChildren().add(p.getShape());
        }

        // Thêm obstacles
        Rectangle pit1 = new Rectangle(80, 200);
        pit1.getStyleClass().add("pit");
        pit1.setX(300);
        pit1.setY(groundLevel - 200);
        root.getChildren().add(pit1);

        Rectangle pit2 = new Rectangle(100, 250);
        pit2.getStyleClass().add("pit");
        pit2.setX(600);
        pit2.setY(groundLevel - 250);
        root.getChildren().add(pit2);

        pits.add(pit1);
        pits.add(pit2);
    }



    private void setupGoal() {
        goal = new Rectangle(15, 100);
        goal.getStyleClass().add("goal");
        goal.setX(screenWidth - 50);
        goal.setY(screenHeight - 400);
        root.getChildren().add(goal);
    }

    public void addNode(javafx.scene.Node node) {
        root.getChildren().add(node);
    }

    public void removeNode(javafx.scene.Node node) {
        root.getChildren().remove(node);
    }

    public Pane getRoot() { return root; }
    public Rectangle getGoal() { return goal; }
    public List<Platform> getPlatforms() { return platforms; }
    public double getGroundLevel() { return groundLevel; }
    public double getScreenWidth() { return screenWidth; }
    public double getScreenHeight() { return screenHeight; }
    public List<Rectangle> getPits() {return pits; }

}