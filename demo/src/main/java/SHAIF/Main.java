package SHAIF;

import SHAIF.controller.*;
import SHAIF.database.DatabaseConnection;
import SHAIF.model.*;
import SHAIF.screen.*;
import SHAIF.view.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private Stage primaryStage;
    private MenuScreen menuScreen;
    private Scene gameScene;
    private GameLoop currentGameLoop;
    private int currentMapId = 1; // Default map

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Test database connection
        DatabaseConnection.getConnection();

        // Load game data khi khởi động
        GameData gameData = GameData.getInstance();
        gameData.load();

        // Load coins vào shop
        UpgradeShop.getInstance().setCoins(gameData.getCoins());

        // Hiển thị menu trước
        showMenu();

        primaryStage.setTitle("Shape Shifter Platformer");

        // Set fullscreen khi chạy game
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    private void showMenu() {
        menuScreen = new MenuScreen(primaryStage);
        menuScreen.setOnPlayCallback(this::startGame);
        menuScreen.setOnLevelSelectCallback(this::showLevelSelect);
        menuScreen.setOnSettingsCallback(this::showSettings);
        menuScreen.setOnShopCallback(this::showShop);
        menuScreen.setOnAchievementsCallback(this::showAchievements);
        menuScreen.show();
    }

    private void showLevelSelect() {
        LevelSelectScreen levelSelect = new LevelSelectScreen(primaryStage);
        levelSelect.setOnBackCallback(this::showMenu);
        levelSelect.setOnLevelSelectedCallback(this::startGame);
        levelSelect.refresh();
        levelSelect.show();
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

    private void startGame() {
        System.out.println("=== Starting Game ===");

        // STOP game loop cũ nếu đang chạy
        if (currentGameLoop != null) {
            currentGameLoop.stop();
            currentGameLoop = null;
        }

        // Load game data
        GameData.getInstance().load();

        // Lấy current level để load map tương ứng
        int currentLevel = LevelManager.getInstance().getCurrentLevel();
        currentMapId = currentLevel; // Map ID tương ứng với level

        System.out.println("Loading Level: " + currentLevel + ", Map ID: " + currentMapId);

        // Khởi tạo View từ database (TẠO MỚI HOÀN TOÀN)
        GameView gameView = new GameView(currentMapId);

        Group gameRoot = new Group();
        gameRoot.getChildren().clear(); // Clear children cũ
        gameRoot.getChildren().add(gameView.getRoot());

        // Khởi tạo Player
        Player player = new Player(100, 680);
        player.setGroundLevel(gameView.getGroundLevel());

        // Khởi tạo Enemies từ map data (DATABASE)
        List<Enemy> enemies = new ArrayList<>();
        List<EnemyData> enemiesData = gameView.getEnemiesData();

        System.out.println("Loading " + enemiesData.size() + " enemies from database");

        if (!enemiesData.isEmpty()) {
            for (EnemyData enemyData : enemiesData) {
                // Convert string type sang EnemyType enum
                EnemyType type = EnemyType.valueOf(enemyData.getEnemyType());

                Enemy enemy = new Enemy(
                        enemyData.getX(),
                        enemyData.getY(),
                        type
                );
                enemy.setTargetPlayer(player);
                enemies.add(enemy);

                // Add enemy shape vào view
                gameView.addNode(enemy.getShape());

                System.out.println("Enemy created: " + type + " at (" + enemyData.getX() + ", " + enemyData.getY() + ")");
            }
        } else {
            // Fallback nếu không có enemy trong database
            System.out.println("No enemies in database, creating default enemy");
            Enemy defaultEnemy = new Enemy(600, 300);
            defaultEnemy.setTargetPlayer(player);
            enemies.add(defaultEnemy);
            gameView.addNode(defaultEnemy.getShape());
        }

        // Lấy enemy đầu tiên để truyền vào GameLoop (backward compatibility)
        Enemy primaryEnemy = enemies.isEmpty() ? null : enemies.get(0);

        Bullet bullet = new Bullet();

        // Add shapes vào view
        gameView.addNode(player.getCurrentShape());
        gameView.addNode(bullet.getShape());

        // Áp dụng upgrades
        applyUpgradesToPlayer(player);

        // Khởi tạo Controllers
        DashController dashController = new DashController(player, gameView);
        KeyInput keyInput = new KeyInput(player, dashController, gameView);

        // Khởi tạo GameStats và HUD
        GameStats stats = new GameStats();
        GameHUD hud = new GameHUD(stats, player);
        gameRoot.getChildren().add(hud.getRoot());

        // Khởi tạo Combo System
        ComboSystem comboSystem = new ComboSystem();
        keyInput.setComboSystem(comboSystem);

        // Khởi tạo GameLoop với primary enemy
        GameLoop gameLoop = new GameLoop(gameView, player, primaryEnemy, bullet, dashController,
                primaryStage, menuScreen, stats, hud, comboSystem);
        currentGameLoop = gameLoop;

        // Setup Scene
        Scene scene = new Scene(gameRoot, 1280, 720);

        Scale scale = new Scale(1, 1);
        gameRoot.getTransforms().clear(); // Clear transforms cũ
        gameRoot.getTransforms().add(scale);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            scale.setX(newVal.doubleValue() / 1280);
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            scale.setY(newVal.doubleValue() / 720);
        });

        // Load CSS
        String css = getClass().getResource("/SHAIF/styles.css").toExternalForm();
        scene.getStylesheets().clear(); // Clear stylesheets cũ
        scene.getStylesheets().add(css);

        keyInput.setupInput(scene);

        // Setup Pause Screen
        PauseScreen pauseScreen = new PauseScreen(primaryStage);
        pauseScreen.setOnResumeCallback(() -> {
            primaryStage.setScene(gameScene);
            gameLoop.resume();
            stats.resume();
        });
        pauseScreen.setOnRestartCallback(this::startGame);
        pauseScreen.setOnQuitCallback(() -> {
            gameLoop.stop();
            GameData.getInstance().save();
            showMenu();
        });

        keyInput.setPauseScreen(pauseScreen);
        keyInput.setOnPauseCallback(() -> {
            gameLoop.pause();
            stats.pause();
            pauseScreen.show();
        });

        // Bắt đầu game loop
        gameLoop.start();

        // Chuyển sang game scene
        primaryStage.setScene(scene);
        gameScene = scene;

        System.out.println("=== Game Started Successfully ===");
    }

    private void applyUpgradesToPlayer(Player player) {
        UpgradeShop shop = UpgradeShop.getInstance();

        // Max Health upgrade
        UpgradeShop.Upgrade maxHealth = shop.getUpgrade("max_health");
        // Note: Có thể thêm setMaxHits() vào Player class nếu cần

        // Walk Speed upgrade
        UpgradeShop.Upgrade walkSpeed = shop.getUpgrade("walk_speed");
        // Note: Có thể thêm setWalkSpeed() vào Player class nếu cần

        // Jump Height upgrade
        UpgradeShop.Upgrade jumpHeight = shop.getUpgrade("jump_height");
        // Note: Có thể thêm setJumpForce() vào Player class nếu cần
    }

    public static void main(String[] args) {
        launch(args);
    }
}