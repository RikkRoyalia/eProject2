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
    private final List<Platform> platforms;
    private final List<Item> items;
    private final List<Rectangle> obstacles;
    private final List<Rectangle> pits;
    private double screenWidth;
    private double screenHeight;
    private double groundLevel;
    private MapData currentMapData;

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

        // Load map data từ database
        loadMapFromDatabase(mapId);
    }

    /**
     * Load map data từ database và setup game view
     */
    private void loadMapFromDatabase(int mapId) {
        System.out.println("\n=== LOADING MAP " + mapId + " ===");

        // XÓA HẾT NỘI DUNG CŨ TRƯỚC KHI LOAD MỚI
        root.getChildren().clear();
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
                System.err.println("  ❌ Invalid item type: " + itemTypeStr);
            }
        }
        System.out.println("✓ Added " + itemCount + " items to scene");

        System.out.println("\n=== Map loaded successfully ===");
        System.out.println("Total objects in scene: " + root.getChildren().size());
        System.out.println("================================\n");
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