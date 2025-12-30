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

    /**
     * Constructor mới - load từ database
     */
    public GameView(int mapId) {
        root = new Pane();
        root.getStyleClass().add("game-root");

        platforms = new ArrayList<>();
        obstacles = new ArrayList<>();
        pits = new ArrayList<>();
        items = new ArrayList<>();

        // Load map data từ database
        loadMapFromDatabase(mapId);
    }

    /**
     * Load map data từ database và setup game view
     */
    private void loadMapFromDatabase(int mapId) {
        // XÓA HẾT NỘI DUNG CŨ TRƯỚC KHI LOAD MỚI
        root.getChildren().clear();
        platforms.clear();
        obstacles.clear();
        pits.clear();
        items.clear();

        currentMapData = MapDAO.loadMap(mapId);

        if (currentMapData == null) {
            System.err.println("Failed to load map! Using default settings.");
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            screenWidth = screenBounds.getWidth();
            screenHeight = screenBounds.getHeight();
            groundLevel = screenHeight - 40;
            setupGoal();
            return;
        }

        // Set screen dimensions từ database
        screenWidth = currentMapData.getScreenWidth();
        screenHeight = currentMapData.getScreenHeight();
        groundLevel = currentMapData.getGroundLevel();

        root.setPrefSize(screenWidth, screenHeight);

        // Load platforms từ database
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

        // Load items từ database - TẠO MỚI HOÀN TOÀN
        for (ItemData iData : currentMapData.getItems()) {
            Item item = new Item(
                    iData.getX(),
                    iData.getY(),
                    iData.getItemTypeEnum()
            );
            // Đảm bảo item được activate
            item.activate();
            items.add(item);
            root.getChildren().add(item.getShape());
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

        System.out.println("=== Map loaded into GameView ===");
        System.out.println("Map: " + currentMapData.getMapName());
        System.out.println("Ground level: " + groundLevel);
        System.out.println("Platforms: " + platforms.size());
        System.out.println("Obstacles: " + obstacles.size());
        System.out.println("Items loaded: " + items.size());
        System.out.println("================================");
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

    private void setupGoal() {
        goal = new Rectangle(15, 100);
        goal.getStyleClass().add("goal");
        goal.setX(screenWidth - 50);
        goal.setY(screenHeight - 400);
        root.getChildren().add(goal);
    }

    public void addNode(javafx.scene.Node node) {
        if (!root.getChildren().contains(node)) {
            root.getChildren().add(node);
        }
    }

    public void removeNode(javafx.scene.Node node) {
        root.getChildren().remove(node);
    }

    /**
     * Reset tất cả items về trạng thái ban đầu
     */
    public void resetItems() {
        for (Item item : items) {
            item.activate();
        }
    }

    // Getters
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