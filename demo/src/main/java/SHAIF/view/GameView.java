package SHAIF.view;

import SHAIF.database.MapDAO;
import SHAIF.model.*;
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
    private final List<Item> items;
    private final List<Rectangle> obstacles;
    private final List<Rectangle> pits;
    private double screenWidth;
    private double screenHeight;
    private double groundLevel;
    private MapData currentMapData;

    // Constructor mặc định - không load từ DB
    public GameView() {
        screenWidth = 1280;
        screenHeight = 720;
        groundLevel = 680;

        root = new Pane();
        root.setPrefSize(screenWidth, screenHeight);
        root.getStyleClass().add("game-root");

        platforms = new ArrayList<>();
        pits = new ArrayList<>();
        obstacles = new ArrayList<>();
        items = new ArrayList<>();

//        setupPlatforms();
        setupGoal();
        setupItems();
    }

    // Constructor mới - load từ database
    public GameView(int mapId) {
        root = new Pane();
        root.getStyleClass().add("game-root");

        platforms = new ArrayList<>();
        obstacles = new ArrayList<>();
        pits = new ArrayList<>();
        items = new ArrayList<>();

        // Load map data từ database
        loadMapFromDatabase(mapId);
        setupItems();
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
//            setupPlatforms();
            setupGoal();
            return;
        }

        // Set screen dimensions từ database
        screenWidth = currentMapData.getScreenWidth();
        screenHeight = currentMapData.getScreenHeight();
        groundLevel = currentMapData.getGroundLevel();

        root.setPrefSize(screenWidth, screenHeight);

        // Load platforms từ database (bao gồm cả ground)
        for (PlatformData pData : currentMapData.getPlatforms()) {
            Platform platform = new Platform(
                    pData.getX(),
                    pData.getY(),
                    pData.getWidth(),
                    pData.getHeight()
            );

            // Nếu là ground platform, thêm style class đặc biệt
            if (pData.isGround()) {
                platform.getShape().getStyleClass().clear();
                platform.getShape().getStyleClass().add("platform");
            }

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
                    pits.add(obstacle);
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
        System.out.println("Ground level: " + groundLevel);
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
//    private void setupPlatforms() {
//        Rectangle ground = new Rectangle(screenWidth, 40);
//        ground.getStyleClass().add("platform");
//        ground.setY(groundLevel);
//        root.getChildren().add(ground);
//
//        // Tầng 1: Platforms thấp
//        Platform low1 = new Platform(500, 180, 150, 20);
//        Platform low2 = new Platform(600, 260, 150, 20);
//
//        // Tầng 2: Platforms trung bình
//        Platform mid1 = new Platform(550, 340, 150, 20);
//        Platform mid2 = new Platform(600, 420, 150, 20);
//
//        // Tầng 3: Platforms cao
//        Platform high1 = new Platform(800, 500, 150, 20);
//        Platform high2 = new Platform(750, 600, 150, 20);
//
//        platforms.add(low1);
//        platforms.add(low2);
//        platforms.add(mid1);
//        platforms.add(mid2);
//        platforms.add(high1);
//        platforms.add(high2);
//
//        for (Platform p : platforms) {
//            root.getChildren().add(p.getShape());
//        }

        // Thêm obstacles
//        double pitWidth = 80;
//        double pitHeight = 40;
//
//        Rectangle pit1 = new Rectangle(pitWidth, pitHeight);
//        pit1.getStyleClass().add("pit");
//        pit1.setX(300);
//        pit1.setY(groundLevel);
//        root.getChildren().add(pit1);
//        pits.add(pit1);
//
//        Rectangle pit2 = new Rectangle(100, 50);
//        pit2.getStyleClass().add("pit");
//        pit2.setX(600);
//        pit2.setY(groundLevel);
//        root.getChildren().add(pit2);
//        pits.add(pit2);
//    }

    private void setupItems() {
        // Health items trên các platforms
        Item health1 = new Item(550, 170, ItemType.HEALTH); // Trên platform low1
        Item health2 = new Item(650, 250, ItemType.HEALTH); // Trên platform low2

        // Coins rải rác
        Item coin1 = new Item(400, 650, ItemType.COIN);
        Item coin2 = new Item(700, 650, ItemType.COIN);
        Item coin3 = new Item(600, 330, ItemType.COIN); // Trên platform mid1
        Item coin4 = new Item(650, 410, ItemType.COIN); // Trên platform mid2
        Item coin5 = new Item(850, 490, ItemType.COIN); // Trên platform high1

        // Power-ups
        Item dashBoost = new Item(800, 570, ItemType.DASH_BOOST); // Trên platform high2
        Item shield = new Item(550, 330, ItemType.SHIELD); // Trên platform mid1
        Item speedBoost = new Item(750, 490, ItemType.SPEED_BOOST); // Trên platform high1
        Item doubleJump = new Item(600, 250, ItemType.DOUBLE_JUMP); // Trên platform low2

        items.add(health1);
        items.add(health2);
        items.add(coin1);
        items.add(coin2);
        items.add(coin3);
        items.add(coin4);
        items.add(coin5);
        items.add(dashBoost);
        items.add(shield);
        items.add(speedBoost);
        items.add(doubleJump);

        // Thêm vào view
        for (Item item : items) {
            root.getChildren().add(item.getShape());
        }
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
    public List<Rectangle> getPits() { return pits; }
    public List<Item> getItems() { return items; }
}