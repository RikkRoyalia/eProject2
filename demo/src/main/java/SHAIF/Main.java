package SHAIF;

import SHAIF.controller.*;
import SHAIF.database.DatabaseConnection;
import SHAIF.model.*;
import SHAIF.screen.*;
import SHAIF.util.MinimapTestUtil;
import SHAIF.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * FIXED Main.java
 * Key fixes:
 * 1. Proper GameView initialization with player reference
 * 2. Correct screen bounds setup
 * 3. Fixed startGame() reload logic
 */
public class Main extends Application {

    private Stage primaryStage;
    private MenuScreen menuScreen;
    private Scene gameScene;
    private MetroidvaniaGameLoop currentGameLoop;
    private WorldMap worldMap;
    private AbilityManager abilityManager;
    private Pane gameRoot;

    private Sound sound = new Sound();

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Test database
        DatabaseConnection.getConnection();

        // Initialize systems
        worldMap = WorldMap.getInstance();
        abilityManager = AbilityManager.getInstance();

        // Load game data
        GameData gameData = GameData.getInstance();
        gameData.load();

        UpgradeShop.getInstance().setCoins(gameData.getCoins());

        showMenu();

        primaryStage.setTitle("Shape Shifter - Metroidvania");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void showMenu() {
        menuScreen = new MenuScreen(primaryStage);
        menuScreen.setOnPlayCallback(this::showNewGameOrContinue);
        menuScreen.setOnSettingsCallback(this::showSettings);
        menuScreen.setOnShopCallback(this::showShop);
        menuScreen.setOnAchievementsCallback(this::showAchievements);
        menuScreen.show();
    }

    private void showNewGameOrContinue() {
        if (MetroidvaniaSaveSystem.hasSaveFile()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION
            );
            alert.setTitle("Load Game");
            alert.setHeaderText("Continue your adventure?");
            alert.setContentText("A save file was detected.");

            javafx.scene.control.ButtonType continueBtn =
                    new javafx.scene.control.ButtonType("Continue");
            javafx.scene.control.ButtonType newGameBtn =
                    new javafx.scene.control.ButtonType("New Game");
            javafx.scene.control.ButtonType cancelBtn =
                    javafx.scene.control.ButtonType.CANCEL;

            alert.getButtonTypes().setAll(continueBtn, newGameBtn, cancelBtn);

            alert.showAndWait().ifPresent(response -> {
                if (response == continueBtn) {
                    loadGame();
                } else if (response == newGameBtn) {
                    confirmNewGame();
                }
            });
        } else {
            startNewGame();
        }
    }

    private void confirmNewGame() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION
        );
        alert.setTitle("New Game");
        alert.setHeaderText("Start a new adventure?");
        alert.setContentText("This will delete your current save file.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                MetroidvaniaSaveSystem.deleteSave();
                startNewGame();
            }
        });
    }

    private void startNewGame() {
        worldMap = WorldMap.getInstance();
        abilityManager.reset();

        worldMap.transitionToRoom("starting_area", 100, 600);
        sound.playMusic(0);
        startGame();
    }

    private void loadGame() {
        SaveData saveData = MetroidvaniaSaveSystem.loadGame();
        if (saveData == null) {
            System.err.println("Failed to load save file");
            startNewGame();
            return;
        }

        worldMap.transitionToRoom(
                saveData.getCurrentRoomId(),
                saveData.getPlayerX(),
                saveData.getPlayerY()
        );

        for (String roomId : saveData.getDiscoveredRooms()) {
            Room room = worldMap.getRooms().get(roomId);
            if (room != null) {
                room.setDiscovered(true);
            }
        }

        String abilitiesData = String.join(",", saveData.getUnlockedAbilities());
        abilityManager.deserialize(abilitiesData);

        System.out.println("Game loaded successfully!");
        System.out.println("Progress: " + abilityManager.getCompletionPercentage() + "%");

        startGame();
    }

    private void showSettings() {
        SettingsScreen settingsScreen = new SettingsScreen(primaryStage);
        settingsScreen.setOnBackCallback(this::showMenu);
        settingsScreen.show();
    }

    private void showShop() {
        ShopScreen shopScreen = new ShopScreen(primaryStage);
        shopScreen.setOnBackCallback(this::showMenu);
        shopScreen.show();
    }

    private void showAchievements() {
        AchievementsScreen achievementsScreen = new AchievementsScreen(primaryStage);
        achievementsScreen.setOnBackCallback(this::showMenu);
        achievementsScreen.show();
    }

    /**
     * FIX: Properly start/restart game
     */
    private void startGame() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    STARTING METROIDVANIA GAME         ║");
        System.out.println("╚════════════════════════════════════════╝");

        // Stop old game loop
        if (currentGameLoop != null) {
            System.out.println("⏹️  Stopping previous game loop...");
            currentGameLoop.stop();
            currentGameLoop = null;
        }

        // Get current room
        Room currentRoom = worldMap.getCurrentRoom();
        currentRoom.setDiscovered(true);

        int mapId = currentRoom.getMapId();
        System.out.println("\n📍 Current Room: " + currentRoom.getName());
        System.out.println("   Map ID: " + mapId);
        System.out.println("   World Position: (" + worldMap.getPlayerWorldX() + ", " + worldMap.getPlayerWorldY() + ")");

        // Initialize GameView
        System.out.println("\n🎮 Initializing GameView...");
        GameView gameView = new GameView(mapId);

        if (gameView.getPlatforms().isEmpty()) {
            System.err.println("⚠️  WARNING: No platforms loaded!");
        } else {
            System.out.println("✓ GameView initialized with " + gameView.getPlatforms().size() + " platforms");
        }

        // Create game root
        gameRoot = new Pane();
        gameRoot.getChildren().clear();
        gameRoot.getChildren().add(gameView.getRoot());
        System.out.println("✓ Game root created");

        // Initialize Player
        Player player = new Player(
                worldMap.getPlayerWorldX(),
                worldMap.getPlayerWorldY()
        );

        // CRITICAL: Set player reference in GameView BEFORE setting bounds
        gameView.setPlayer(player);

        // Set ground level
        player.setGroundLevel(gameView.getGroundLevel());

        // Set screen bounds
        player.setScreenBounds(
                10,
                gameView.getScreenWidth() - 10,
                0,
                gameView.getScreenHeight()
        );

        System.out.println("✓ Player created at (" + player.getX() + ", " + player.getY() + ")");
        System.out.println("  Ground level: " + gameView.getGroundLevel());
        System.out.println("  Screen bounds: 10 to " + (gameView.getScreenWidth() - 10));

        // Initialize Enemies
        List<Enemy> enemies = new ArrayList<>();
        List<EnemyData> enemiesData = gameView.getEnemiesData();

        System.out.println("Loading " + enemiesData.size() + " enemies");

        for (EnemyData enemyData : enemiesData) {
            EnemyType type = EnemyType.valueOf(enemyData.getEnemyType());
            Enemy enemy = new Enemy(
                    enemyData.getX(),
                    enemyData.getY(),
                    type
            );
            enemy.setTargetPlayer(player);
            enemies.add(enemy);
            gameView.addNode(enemy.getShape());
        }

        Enemy primaryEnemy = enemies.isEmpty() ? null : enemies.get(0);
        Bullet bullet = new Bullet();

        // Add shapes to view
        gameView.addNode(player.getCurrentShape());
        gameView.addNode(bullet.getShape());

        // Apply upgrades
        applyUpgradesToPlayer(player);

        // Initialize Controllers
        DashController dashController = new DashController(player, gameView);
        KeyInput keyInput = new KeyInput(player, dashController, gameView);

        // Initialize GameStats and HUD
        GameStats stats = new GameStats();
        GameHUD hud = new GameHUD(stats, player);
        gameRoot.getChildren().add(hud.getRoot());

        // Add Minimap
        Minimap minimap = new Minimap(200, 150);
        minimap.getCanvas().setLayoutX(1060);
        minimap.getCanvas().setLayoutY(20);
        gameRoot.getChildren().add(minimap.getCanvas());

        // Initialize Combo System
        ComboSystem comboSystem = new ComboSystem();
        keyInput.setComboSystem(comboSystem);

        // Initialize MetroidvaniaGameLoop
        MetroidvaniaGameLoop gameLoop = new MetroidvaniaGameLoop(
                gameView, player, primaryEnemy, bullet, dashController,
                primaryStage, menuScreen, stats, hud, comboSystem,
                minimap, worldMap, abilityManager, gameRoot
        );
        currentGameLoop = gameLoop;

        // Setup Scene
        Scene scene = new Scene(gameRoot, 1280, 720);

        Scale scale = new Scale(1, 1);
        gameRoot.getTransforms().clear();
        gameRoot.getTransforms().add(scale);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            scale.setX(newVal.doubleValue() / 1280);
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            scale.setY(newVal.doubleValue() / 720);
        });

        // Load CSS
        String css = getClass().getResource("/SHAIF/styles.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);

        keyInput.setupInput(scene);

        // Setup Pause Screen
        PauseScreen pauseScreen = createPauseScreen(gameLoop, stats);
        keyInput.setPauseScreen(pauseScreen);
        keyInput.setOnPauseCallback(() -> {
            gameLoop.pause();
            stats.pause();
            pauseScreen.show();
        });

        // Start game loop
        gameLoop.start();

        // Switch to game scene
        primaryStage.setScene(scene);
        gameScene = scene;

        System.out.println("\n✅ METROIDVANIA GAME STARTED");
        System.out.println("   Current Room: " + currentRoom.getName());
        System.out.println("   Player Position: (" + player.getX() + ", " + player.getY() + ")");
        System.out.println("   Abilities Unlocked: " + abilityManager.getUnlockedAbilities().size());
        System.out.println("   Completion: " + abilityManager.getCompletionPercentage() + "%");
        System.out.println("════════════════════════════════════════\n");
    }

    private PauseScreen createPauseScreen(MetroidvaniaGameLoop gameLoop, GameStats stats) {
        PauseScreen pauseScreen = new PauseScreen(primaryStage);
        sound.stopMusic();

        pauseScreen.setOnResumeCallback(() -> {
            primaryStage.setScene(gameScene);
            gameLoop.resume();
            stats.resume();
        });

        pauseScreen.setOnRestartCallback(() -> {
            gameLoop.stop();
            SaveData saveData = MetroidvaniaSaveSystem.loadGame();
            if (saveData != null && saveData.getLastSavePointRoom() != null) {
                worldMap.transitionToRoom(
                        saveData.getLastSavePointRoom(),
                        saveData.getLastSavePointX(),
                        saveData.getLastSavePointY()
                );
            }
            startGame();
        });

        pauseScreen.setOnQuitCallback(() -> {
            gameLoop.stop();

            // Auto-save on quit
            SaveData data = new SaveData();
            data.setCurrentRoomId(worldMap.getCurrentRoomId());
            data.setPlayerX(gameLoop.getPlayer().getX());
            data.setPlayerY(gameLoop.getPlayer().getY());
            data.setHealth(gameLoop.getPlayer().getMaxHits() -
                    gameLoop.getPlayer().getHitCount());

            for (Room room : worldMap.getRooms().values()) {
                if (room.isDiscovered()) {
                    data.addDiscoveredRoom(room.getId());
                }
            }

            for (String ability : abilityManager.getUnlockedAbilities()) {
                data.addUnlockedAbility(ability);
            }

            data.setTotalPlayTime((int) stats.getPlayTime());
            data.setCompletionPercentage(abilityManager.getCompletionPercentage());

            MetroidvaniaSaveSystem.saveGame(data);
            GameData.getInstance().save();

            showMenu();
        });

        return pauseScreen;
    }

    private void applyUpgradesToPlayer(Player player) {
        UpgradeShop shop = UpgradeShop.getInstance();

        UpgradeShop.Upgrade maxHealth = shop.getUpgrade("max_health");
        if (maxHealth != null && maxHealth.getLevel() > 0) {
            // TODO: Apply max health multiplier
        }

        UpgradeShop.Upgrade walkSpeed = shop.getUpgrade("walk_speed");
        if (walkSpeed != null && walkSpeed.getLevel() > 0) {
            // TODO: Apply walk speed multiplier
        }

        UpgradeShop.Upgrade jumpHeight = shop.getUpgrade("jump_height");
        if (jumpHeight != null && jumpHeight.getLevel() > 0) {
            // TODO: Apply jump height multiplier
        }
    }

    public static void main(String[] args) {
        WorldMap.getInstance();
        MinimapTestUtil.runAllTests();
        launch(args);
    }
}