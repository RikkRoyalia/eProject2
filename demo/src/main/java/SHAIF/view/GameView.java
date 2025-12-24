package SHAIF.view;

import SHAIF.database.MapDAO;
import SHAIF.model.*;
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
    private final List<Rectangle> obstacles;
    private double screenWidth;
    private double screenHeight;
    private double groundLevel;
    private MapData currentMapData;

    // Constructor mặc định - không load từ DB
    public GameView() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        screenWidth = screenBounds.getWidth();
        screenHeight = screenBounds.getHeight();
        groundLevel = screenHeight - 40;

        root = new Pane();
        root.setPrefSize(screenWidth, screenHeight);
        root.getStyleClass().add("game-root");

        platforms = new ArrayList<>();
        obstacles = new ArrayList<>();

        setupPlatforms();
        setupGoal();
    }

    // Constructor mới - load từ database
    public GameView(int mapId) {
        root = new Pane();
        root.getStyleClass().add("game-root");

        platforms = new ArrayList<>();
        obstacles = new ArrayList<>();

        // Load map data từ database
        loadMapFromDatabase(mapId);
    }

    /**
     * Load map data từ database và setup game view
     */
    private void loadMapFromDatabase(int mapId) {
        currentMapData = MapDAO.loadMap(mapId);

        if (currentMapData == null) {
            System.err.println("Failed to load map! Using default settings.");
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            screenWidth = screenBounds.getWidth();
            screenHeight = screenBounds.getHeight();
            groundLevel = screenHeight - 40;
            setupPlatforms();
            setupGoal();
            return;
        }

        // Set screen dimensions từ database
        screenWidth = currentMapData.getScreenWidth();
        screenHeight = currentMapData.getScreenHeight();
        groundLevel = currentMapData.getGroundLevel();

        root.setPrefSize(screenWidth, screenHeight);

        // Setup ground
        Rectangle ground = new Rectangle(screenWidth, screenHeight - groundLevel);
        ground.getStyleClass().add("platform");
        ground.setY(groundLevel);
        root.getChildren().add(ground);

        // Load platforms từ database
        for (PlatformData pData : currentMapData.getPlatforms()) {
            Platform platform = new Platform(
                    pData.getX(),
                    pData.getY(),
                    pData.getWidth(),
                    pData.getHeight()
            );
            platforms.add(platform);
            root.getChildren().add(platform.getShape());
        }

        // Load obstacles từ database
        for (ObstacleData oData : currentMapData.getObstacles()) {
            Rectangle obstacle = new Rectangle(oData.getWidth(), oData.getHeight());

            // Set style class dựa trên type
            switch (oData.getObstacleType()) {
                case "pit":
                    obstacle.getStyleClass().add("pit");
                    break;
                case "spike":
                    obstacle.getStyleClass().add("spike");
                    break;
                case "wall":
                    obstacle.getStyleClass().add("wall");
                    break;
                default:
                    obstacle.getStyleClass().add("obstacle");
            }

            obstacle.setX(oData.getX());
            obstacle.setY(oData.getY());
            obstacles.add(obstacle);
            root.getChildren().add(obstacle);
        }

        // Setup goal từ database
        goal = new Rectangle(
                currentMapData.getGoalWidth(),
                currentMapData.getGoalHeight()
        );
        goal.getStyleClass().add("goal");
        goal.setX(currentMapData.getGoalX());
        goal.setY(currentMapData.getGoalY());
        root.getChildren().add(goal);

        System.out.println("Map '" + currentMapData.getMapName() + "' loaded into GameView!");
    }

    /**
     * Lấy danh sách enemies từ map data
     */
    public List<EnemyData> getEnemiesData() {
        if (currentMapData != null) {
            return currentMapData.getEnemies();
        }
        return new ArrayList<>();
    }

    // Setup cũ cho backward compatibility
    private void setupPlatforms() {
        Rectangle ground = new Rectangle(screenWidth, 40);
        ground.getStyleClass().add("platform");
        ground.setY(groundLevel);
        root.getChildren().add(ground);

        Platform low1 = new Platform(550, 180, 150, 20);
        Platform low2 = new Platform(600, 260, 150, 20);
        Platform mid1 = new Platform(550, 340, 150, 20);
        Platform mid2 = new Platform(600, 420, 150, 20);
        Platform high1 = new Platform(800, 500, 150, 20);
        Platform high2 = new Platform(750, 580, 150, 20);

        platforms.add(low1);
        platforms.add(low2);
        platforms.add(mid1);
        platforms.add(mid2);
        platforms.add(high1);
        platforms.add(high2);

        for (Platform p : platforms) {
            root.getChildren().add(p.getShape());
        }

        Rectangle pit1 = new Rectangle(80, 200);
        pit1.getStyleClass().add("pit");
        pit1.setX(300);
        pit1.setY(groundLevel - 200);
        obstacles.add(pit1);
        root.getChildren().add(pit1);

        Rectangle pit2 = new Rectangle(100, 250);
        pit2.getStyleClass().add("pit");
        pit2.setX(600);
        pit2.setY(groundLevel - 250);
        obstacles.add(pit2);
        root.getChildren().add(pit2);
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
    public List<Rectangle> getObstacles() { return obstacles; }
    public double getGroundLevel() { return groundLevel; }
    public double getScreenWidth() { return screenWidth; }
    public double getScreenHeight() { return screenHeight; }
    public MapData getCurrentMapData() { return currentMapData; }
}