package SHAIF.view;

import SHAIF.database.MapDAO;
import SHAIF.model.*;
import SHAIF.model.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;

public class GameView {
    private final Pane root;
    private final List<Platform> platforms;
    private final List<Item> items;
    private final List<Rectangle> obstacles;
    private final List<Rectangle> pits;
    private double screenWidth;
    private double screenHeight;
    private double groundLevel;
    private MapData currentMapData;
    private Player player;

    // Background layer
    private ImageView backgroundLayer;

    /**
     * Constructor - load từ database
     */
    public GameView(int mapId) {
        root = new Pane();
        root.getStyleClass().add("game-root");

        platforms = new ArrayList<>();
        obstacles = new ArrayList<>();
        pits = new ArrayList<>();
        items = new ArrayList<>();

        // Khởi tạo background layer
        backgroundLayer = new ImageView();
        backgroundLayer.setPreserveRatio(false);
        backgroundLayer.setSmooth(true);

        // QUAN TRỌNG: Thêm background vào root TRƯỚC KHI load map
        // để background ở dưới cùng
        root.getChildren().add(backgroundLayer);

        // Load map data từ database
        loadMapFromDatabase(mapId);
    }

    public void setPlayer(Player player) {
        this.player = player;
        // Update player ground level immediately
        if (player != null) {
            updatePlayerForCurrentMap();
        }
    }

    /**
     * Load map data từ database và setup game view
     */
    private void loadMapFromDatabase(int mapId) {
        System.out.println("\n=== LOADING MAP " + mapId + " ===");

        // XÓA HẾT NỘI DUNG CŨ TRƯỚC KHI LOAD MỚI
        // NHƯNG GIỮ LẠI background layer
        root.getChildren().clear();
        root.getChildren().add(backgroundLayer); // Add lại background

        platforms.clear();
        obstacles.clear();
        pits.clear();
        items.clear();

        currentMapData = MapDAO.loadMap(mapId);

        if (currentMapData == null) {
            System.err.println("FAILED TO LOAD MAP DATA!");
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            screenWidth = screenBounds.getWidth();
            screenHeight = screenBounds.getHeight();
            groundLevel = screenHeight - 40;
            return;
        }

        System.out.println("✓ MapData loaded: " + currentMapData.getMapName());
        System.out.println("  Screen: " + currentMapData.getScreenWidth() + "x" + currentMapData.getScreenHeight());
        System.out.println("  Platforms in data: " + currentMapData.getPlatforms().size());
        System.out.println("  Hazards in data: " + currentMapData.getObstacles().size());
        System.out.println("  Items in data: " + currentMapData.getItems().size());
        System.out.println("  Enemies in data: " + currentMapData.getEnemies().size());

        // Set screen dimensions từ database
        screenWidth = currentMapData.getScreenWidth();
        screenHeight = currentMapData.getScreenHeight();
        groundLevel = currentMapData.getGroundLevel();

        root.setPrefSize(screenWidth, screenHeight);
        System.out.println("✓ Root pane size set: " + screenWidth + "x" + screenHeight);
        System.out.println("✓ Ground level: " + groundLevel);

        // LOAD BACKGROUND IMAGE
        loadBackgroundImage(mapId);

        // Load platforms từ database
        System.out.println("\n--- Loading Platforms ---");
        int platformCount = 0;
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
                System.out.println("  GROUND platform at (" + pData.getX() + ", " + pData.getY() + ") " +
                        pData.getWidth() + "x" + pData.getHeight());
            } else {
                System.out.println("  Platform at (" + pData.getX() + ", " + pData.getY() + ") " +
                        pData.getWidth() + "x" + pData.getHeight());
            }

            platforms.add(platform);
            root.getChildren().add(platform.getShape());
            platformCount++;
        }
        System.out.println("✓ Added " + platformCount + " platforms to scene");

        // Load hazards từ database
        System.out.println("\n--- Loading Hazards ---");
        int hazardCount = 0;
        for (ObstacleData oData : currentMapData.getObstacles()) {
            Rectangle hazard = new Rectangle(oData.getWidth(), oData.getHeight());

            // Set style class dựa trên type
            String hazardType = oData.getObstacleType().toUpperCase();
            switch (hazardType) {
                case "PIT":
                    hazard.getStyleClass().add("pit");
                    pits.add(hazard);
                    System.out.println("  PIT at (" + oData.getX() + ", " + oData.getY() + ")");
                    break;
                case "SPIKE":
                    hazard.getStyleClass().add("spike");
                    System.out.println("  SPIKE at (" + oData.getX() + ", " + oData.getY() + ")");
                    break;
                case "LAVA":
                    hazard.getStyleClass().add("lava");
                    System.out.println("  LAVA at (" + oData.getX() + ", " + oData.getY() + ")");
                    break;
                default:
                    hazard.getStyleClass().add("obstacle");
                    System.out.println("  OBSTACLE at (" + oData.getX() + ", " + oData.getY() + ")");
            }

            hazard.setX(oData.getX());
            hazard.setY(oData.getY());
            obstacles.add(hazard);
            root.getChildren().add(hazard);
            hazardCount++;
        }
        System.out.println("✓ Added " + hazardCount + " hazards to scene");

        // Load items từ database
        System.out.println("\n--- Loading Items ---");
        int itemCount = 0;
        for (ItemData iData : currentMapData.getItems()) {
            // Convert item type từ database
            String itemTypeStr = iData.getItemType();
            ItemType itemType;

            try {
                // Nếu là BUFF, convert sang một loại buff cụ thể
                if (itemTypeStr.equals("BUFF")) {
                    itemType = ItemType.DASH_BOOST;
                } else if (itemTypeStr.equals("ABILITY")) {
                    itemType = ItemType.DOUBLE_JUMP;
                } else {
                    itemType = ItemType.valueOf(itemTypeStr);
                }

                Item item = new Item(
                        iData.getX(),
                        iData.getY(),
                        itemType
                );

                // Đảm bảo item được activate
                item.activate();
                items.add(item);
                root.getChildren().add(item.getShape());
                itemCount++;

                System.out.println("  Item: " + itemType + " at (" + iData.getX() + ", " + iData.getY() + ")");

            } catch (IllegalArgumentException e) {
                System.err.println(" Invalid item type: " + itemTypeStr);
            }
        }
        System.out.println("✓ Added " + itemCount + " items to scene");

        System.out.println("\n=== Map loaded successfully ===");
        System.out.println("Total objects in scene: " + root.getChildren().size());
        System.out.println("================================\n");

        updatePlayerForCurrentMap();
    }

    /**
     * Load background image dựa trên mapId
     */
    private void loadBackgroundImage(int mapId) {
        try {
            // Thử load background cho map cụ thể
            String imagePath = "/image/background_" + mapId + ".png";
            System.out.println("\n--- Loading Background ---");
            System.out.println("  Trying to load: " + imagePath);

            Image backgroundImage = new Image(getClass().getResourceAsStream(imagePath));

            if (backgroundImage.isError()) {
                System.err.println("  ✖ Failed to load background for map " + mapId);
                loadDefaultBackground();
            } else {
                backgroundLayer.setImage(backgroundImage);
                backgroundLayer.setFitWidth(screenWidth);
                backgroundLayer.setFitHeight(screenHeight);
                System.out.println("  Background loaded successfully!");
                System.out.println("  Image size: " + backgroundImage.getWidth() + "x" + backgroundImage.getHeight());
            }

        } catch (Exception e) {
            System.err.println("  Exception loading background: " + e.getMessage());
            loadDefaultBackground();
        }
    }

    /**
     * Load default background nếu không tìm thấy background cho map
     */
    private void loadDefaultBackground() {
        try {
            String defaultPath = "/image/background_default.png";
            System.out.println("  Loading default background: " + defaultPath);

            Image defaultImage = new Image(getClass().getResourceAsStream(defaultPath));

            if (!defaultImage.isError()) {
                backgroundLayer.setImage(defaultImage);
                backgroundLayer.setFitWidth(screenWidth);
                backgroundLayer.setFitHeight(screenHeight);
                System.out.println("Default background loaded!");
            } else {
                System.err.println("Default background also failed to load");
                // Không có background, để màu nền từ CSS
            }
        } catch (Exception e) {
            System.err.println("Exception loading default background: " + e.getMessage());
        }
    }

    /**
     * Update player state for current map
     * This fixes the "floating player" bug!
     */
    private void updatePlayerForCurrentMap() {
        if (player == null) return;

        System.out.println("\nUpdating player for current map...");

        // Update ground level
        player.setGroundLevel(groundLevel);
        System.out.println("  Ground level set to: " + groundLevel);

        // Update screen bounds
        player.setScreenBounds(0, screenWidth, 0, screenHeight);
        System.out.println("  Screen bounds: 0, " + screenWidth + ", 0, " + screenHeight);

        // Reset velocity
        player.setVelY(0);
        System.out.println("  Velocity reset");

        // Stop any movement/dash
        player.setDashing(false);
        System.out.println("  Dashing stopped");

        System.out.println("Player updated for map!\n");
    }

    /**
     * Reload map (for room transitions)
     * Call this when changing rooms
     */
    public void reloadMap(int mapId) {
        System.out.println("\nReloading map " + mapId + "...");
        loadMapFromDatabase(mapId);
    }

    /**
     * Transition to new map with spawn position
     */
    public void transitionToMap(int mapId, double spawnX, double spawnY) {
        System.out.println("\nTransitioning to map " + mapId + " at (" + spawnX + ", " + spawnY + ")");

        // Load new map
        loadMapFromDatabase(mapId);

        // Set player position
        if (player != null) {
            player.setX(spawnX);
            player.setY(spawnY);
            System.out.println("  Player spawned at: (" + spawnX + ", " + spawnY + ")");
        }
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

    public void addNode(javafx.scene.Node node) {
        if (!root.getChildren().contains(node)) {
            root.getChildren().add(node);
            System.out.println("Node added: " + node.getClass().getSimpleName());
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
    public List<Platform> getPlatforms() { return platforms; }
    public List<Rectangle> getObstacles() { return obstacles; }
    public double getGroundLevel() { return groundLevel; }
    public double getScreenWidth() { return screenWidth; }
    public double getScreenHeight() { return screenHeight; }
    public MapData getCurrentMapData() { return currentMapData; }
    public List<Rectangle> getPits() { return pits; }
    public List<Item> getItems() { return items; }
}